package com.moviequiz.dto.quiz;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAnswerResponse {
    private boolean correct;
    private String correctOptionId;
    private String correctAnswerText;
}
