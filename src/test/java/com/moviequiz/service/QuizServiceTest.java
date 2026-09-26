package com.moviequiz.service;

import com.moviequiz.domain.Actor;
import com.moviequiz.domain.Movie;
import com.moviequiz.domain.MovieActor;
import com.moviequiz.domain.MovieActorId;
import com.moviequiz.dto.quiz.QuizAnswerRequest;
import com.moviequiz.dto.quiz.QuizAnswerResponse;
import com.moviequiz.dto.quiz.QuizOption;
import com.moviequiz.dto.quiz.QuizQuestionResponse;
import com.moviequiz.exception.QuizStateException;
import com.moviequiz.repository.ActorRepository;
import com.moviequiz.repository.MovieActorRepository;
import com.moviequiz.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Exercises {@link QuizService} against mocked repositories so it runs
 * without a real database. Because the service picks one of several
 * question types at random, the assertions cover invariants that hold no
 * matter which type is generated, rather than one specific question.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class QuizServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private ActorRepository actorRepository;
    @Mock
    private MovieActorRepository movieActorRepository;

    private QuizSessionStore sessionStore;
    private QuizService quizService;

    @BeforeEach
    void setUp() {
        sessionStore = new QuizSessionStore();
        quizService = new QuizService(movieRepository, actorRepository, movieActorRepository, sessionStore);

        Movie movie = Movie.builder()
                .id(1L)
                .title("The Matrix")
                .releaseYear(1999)
                .runtimeMinutes(136)
                .averageRating(new BigDecimal("8.7"))
                .numVotes(2_000_000)
                .build();

        Actor lead = Actor.builder().id(1L).name("Keanu Reeves").build();
        Actor second = Actor.builder().id(2L).name("Laurence Fishburne").build();

        MovieActor leadRole = MovieActor.builder()
                .id(new MovieActorId(1L, 1L)).movie(movie).actor(lead).importance(1).characterName("Neo").build();
        MovieActor secondRole = MovieActor.builder()
                .id(new MovieActorId(1L, 2L)).movie(movie).actor(second).importance(2).characterName("Morpheus").build();

        when(movieRepository.findRandomMovie(anyInt(), anyInt(), anyInt())).thenReturn(Optional.of(movie));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(movieRepository.findRandomTitlesExcluding(anyLong(), anyInt()))
                .thenReturn(List.of("Titanic", "Inception", "The Dark Knight", "Fight Club"));
        when(actorRepository.findRandomNamesExcluding(anyLong(), anyInt()))
                .thenReturn(List.of("Tom Hanks", "Brad Pitt", "Al Pacino", "Christian Bale"));
        when(movieActorRepository.findByMovie_IdOrderByImportanceAsc(1L)).thenReturn(List.of(leadRole, secondRole));
        // No dataset large enough for the co-stars question in this fixture.
        when(movieActorRepository.findRandomMovieIdWithCastSizeAtLeast(anyInt(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Optional.empty());
    }

    @Test
    void nextQuestionHasFourUniqueOptions() {
        QuizQuestionResponse question = quizService.nextQuestion(1970, 2030, 10_000);

        assertThat(question.getQuestionId()).isNotBlank();
        assertThat(question.getOptions()).hasSize(4);

        List<String> optionIds = question.getOptions().stream().map(QuizOption::getId).toList();
        assertThat(optionIds).containsExactlyInAnyOrder("1", "2", "3", "4");

        long uniqueTexts = question.getOptions().stream().map(QuizOption::getText).distinct().count();
        assertThat(uniqueTexts).isEqualTo(4);
    }

    @Test
    void correctAnswerIsAcceptedAndQuestionCannotBeReused() {
        QuizQuestionResponse question = quizService.nextQuestion(1970, 2030, 10_000);
        String correctOptionId = correctOptionIdFor(question);

        QuizAnswerRequest correctRequest = new QuizAnswerRequest();
        correctRequest.setQuestionId(question.getQuestionId());
        correctRequest.setSelectedOptionId(correctOptionId);

        QuizAnswerResponse response = quizService.submitAnswer(correctRequest);
        assertThat(response.isCorrect()).isTrue();
        assertThat(response.getCorrectOptionId()).isEqualTo(correctOptionId);

        // Submitting again for the same question should fail - it's single-use.
        assertThatThrownBy(() -> quizService.submitAnswer(correctRequest))
                .isInstanceOf(QuizStateException.class);
    }

    @Test
    void wrongAnswerIsReportedAsIncorrect() {
        QuizQuestionResponse question = quizService.nextQuestion(1970, 2030, 10_000);
        String correctOptionId = correctOptionIdFor(question);
        String wrongOptionId = question.getOptions().stream()
                .map(QuizOption::getId)
                .filter(id -> !id.equals(correctOptionId))
                .findFirst()
                .orElseThrow();

        QuizAnswerRequest request = new QuizAnswerRequest();
        request.setQuestionId(question.getQuestionId());
        request.setSelectedOptionId(wrongOptionId);

        assertThat(quizService.submitAnswer(request).isCorrect()).isFalse();
    }

    /**
     * The correct option isn't exposed on the response DTO by design (the
     * client shouldn't be able to read it off the question payload), so
     * tests peek at the session store directly instead of guessing.
     */
    private String correctOptionIdFor(QuizQuestionResponse question) {
        return sessionStore.peek(question.getQuestionId())
                .orElseThrow(() -> new IllegalStateException("No pending answer for " + question.getQuestionId()))
                .correctOptionId();
    }
}
