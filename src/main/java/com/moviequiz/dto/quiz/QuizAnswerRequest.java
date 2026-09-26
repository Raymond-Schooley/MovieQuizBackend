package com.moviequiz.dto.quiz;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizAnswerRequest {

    @NotBlank
    private String questionId;

    @NotBlank
    private String selectedOptionId;
}
