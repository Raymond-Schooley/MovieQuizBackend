package com.moviequiz.controller;

import com.moviequiz.dto.quiz.QuizAnswerRequest;
import com.moviequiz.dto.quiz.QuizAnswerResponse;
import com.moviequiz.dto.quiz.QuizQuestionResponse;
import com.moviequiz.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/questions/next")
    public QuizQuestionResponse nextQuestion(
            @RequestParam(defaultValue = "1970") int minYear,
            @RequestParam(defaultValue = "2030") int maxYear,
            @RequestParam(defaultValue = "10000") int minVotes) {
        return quizService.nextQuestion(minYear, maxYear, minVotes);
    }

    @PostMapping("/answers")
    public QuizAnswerResponse submitAnswer(@Valid @RequestBody QuizAnswerRequest request) {
        return quizService.submitAnswer(request);
    }
}
