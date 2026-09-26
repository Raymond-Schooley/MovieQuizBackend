package com.moviequiz.service;

import com.moviequiz.domain.Movie;
import com.moviequiz.domain.MovieActor;
import com.moviequiz.dto.quiz.QuizAnswerRequest;
import com.moviequiz.dto.quiz.QuizAnswerResponse;
import com.moviequiz.dto.quiz.QuizOption;
import com.moviequiz.dto.quiz.QuizQuestionResponse;
import com.moviequiz.exception.QuizStateException;
import com.moviequiz.exception.ResourceNotFoundException;
import com.moviequiz.repository.ActorRepository;
import com.moviequiz.repository.MovieActorRepository;
import com.moviequiz.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuizService {

    /** The four question types the quiz can generate, chosen at random. */
    private enum QuestionType {
        LEAD_ACTOR, MOVIE_FOR_ACTOR, RELEASE_YEAR, CO_STARS
    }

    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final MovieActorRepository movieActorRepository;
    private final QuizSessionStore sessionStore;

    public QuizQuestionResponse nextQuestion(int minYear, int maxYear, int minVotes) {
        if (minYear > maxYear) {
            throw new IllegalArgumentException("minYear must be <= maxYear");
        }
        QuestionType[] types = QuestionType.values();
        // Try each question type at most once in random order, in case the
        // dataset can't satisfy the first choice (e.g. too few co-star movies).
        List<QuestionType> order = new ArrayList<>(List.of(types));
        java.util.Collections.shuffle(order);

        for (QuestionType type : order) {
            QuizQuestionResponse question = switch (type) {
                case LEAD_ACTOR -> buildLeadActorQuestion(minYear, maxYear, minVotes);
                case MOVIE_FOR_ACTOR -> buildMovieForActorQuestion(minYear, maxYear, minVotes);
                case RELEASE_YEAR -> buildReleaseYearQuestion(minYear, maxYear, minVotes);
                case CO_STARS -> buildCoStarsQuestion(minYear, maxYear, minVotes);
            };
            if (question != null) {
                return question;
            }
        }
        throw new ResourceNotFoundException(
                "Not enough data to build a quiz question for that filter - try widening the year range");
    }

    public QuizAnswerResponse submitAnswer(QuizAnswerRequest request) {
        QuizSessionStore.PendingAnswer pending = sessionStore.take(request.getQuestionId())
                .orElseThrow(() -> new QuizStateException(
                        "That question has already been answered or has expired - request a new one"));

        boolean correct = pending.correctOptionId().equals(request.getSelectedOptionId());
        return QuizAnswerResponse.builder()
                .correct(correct)
                .correctOptionId(pending.correctOptionId())
                .correctAnswerText(pending.correctAnswerText())
                .build();
    }

    private QuizQuestionResponse buildLeadActorQuestion(int minYear, int maxYear, int minVotes) {
        Movie movie = movieRepository.findRandomMovie(minYear, maxYear, minVotes).orElse(null);
        if (movie == null) {
            return null;
        }
        List<MovieActor> cast = movieActorRepository.findByMovie_IdOrderByImportanceAsc(movie.getId());
        if (cast.isEmpty()) {
            return null;
        }
        String correctAnswer = cast.get(0).getActor().getName();
        List<String> distractors = actorRepository.findRandomNamesExcluding(cast.get(0).getActor().getId(), 6);

        String prompt = "Who was the lead actor in \"" + movie.getTitle() + "\" (" + movie.getReleaseYear() + ")?";
        return buildResponse("LEAD_ACTOR", prompt, correctAnswer, distractors);
    }

    private QuizQuestionResponse buildMovieForActorQuestion(int minYear, int maxYear, int minVotes) {
        Movie movie = movieRepository.findRandomMovie(minYear, maxYear, minVotes).orElse(null);
        if (movie == null) {
            return null;
        }
        List<MovieActor> cast = movieActorRepository.findByMovie_IdOrderByImportanceAsc(movie.getId());
        if (cast.isEmpty()) {
            return null;
        }
        MovieActor lead = cast.get(0);
        String correctAnswer = movie.getTitle();
        List<String> distractors = movieRepository.findRandomTitlesExcluding(movie.getId(), 6);

        String prompt = "Which movie did " + lead.getActor().getName() + " star in?";
        return buildResponse("MOVIE_FOR_ACTOR", prompt, correctAnswer, distractors);
    }

    private QuizQuestionResponse buildReleaseYearQuestion(int minYear, int maxYear, int minVotes) {
        Movie movie = movieRepository.findRandomMovie(minYear, maxYear, minVotes).orElse(null);
        if (movie == null) {
            return null;
        }
        int correctYear = movie.getReleaseYear();
        Set<String> distractors = new LinkedHashSet<>();
        int[] offsets = {-3, -2, -1, 1, 2, 3, 5, -5};
        for (int offset : offsets) {
            if (distractors.size() >= 6) {
                break;
            }
            int candidate = correctYear + offset;
            if (candidate != correctYear) {
                distractors.add(String.valueOf(candidate));
            }
        }

        String prompt = "What year was \"" + movie.getTitle() + "\" released?";
        return buildResponse("RELEASE_YEAR", prompt, String.valueOf(correctYear), new ArrayList<>(distractors));
    }

    private QuizQuestionResponse buildCoStarsQuestion(int minYear, int maxYear, int minVotes) {
        Long movieId = movieActorRepository
                .findRandomMovieIdWithCastSizeAtLeast(minYear, maxYear, minVotes, 2)
                .orElse(null);
        if (movieId == null) {
            return null;
        }
        Movie movie = movieRepository.findById(movieId).orElse(null);
        List<MovieActor> cast = movieActorRepository.findByMovie_IdOrderByImportanceAsc(movieId);
        if (movie == null || cast.size() < 2) {
            return null;
        }
        String actorA = cast.get(0).getActor().getName();
        String actorB = cast.get(1).getActor().getName();
        String correctAnswer = movie.getTitle();
        List<String> distractors = movieRepository.findRandomTitlesExcluding(movieId, 6);

        String prompt = "Which movie starred both " + actorA + " and " + actorB + "?";
        return buildResponse("CO_STARS", prompt, correctAnswer, distractors);
    }

    private QuizQuestionResponse buildResponse(String type, String prompt, String correctAnswer, List<String> distractorPool) {
        Set<String> chosenDistractors = new LinkedHashSet<>();
        for (String distractor : distractorPool) {
            if (chosenDistractors.size() >= 3) {
                break;
            }
            if (!distractor.equalsIgnoreCase(correctAnswer)) {
                chosenDistractors.add(distractor);
            }
        }
        if (chosenDistractors.size() < 3) {
            // Not enough distinct distractors in the dataset for this round.
            return null;
        }

        List<String> answerTexts = new ArrayList<>(chosenDistractors);
        answerTexts.add(correctAnswer);
        java.util.Collections.shuffle(answerTexts);

        List<QuizOption> options = new ArrayList<>();
        String correctOptionId = null;
        for (int i = 0; i < answerTexts.size(); i++) {
            String optionId = String.valueOf(i + 1);
            options.add(QuizOption.builder().id(optionId).text(answerTexts.get(i)).build());
            if (answerTexts.get(i).equals(correctAnswer)) {
                correctOptionId = optionId;
            }
        }

        String questionId = UUID.randomUUID().toString();
        sessionStore.put(questionId, correctOptionId, correctAnswer);

        return QuizQuestionResponse.builder()
                .questionId(questionId)
                .questionType(type)
                .prompt(prompt)
                .options(options)
                .build();
    }
}
