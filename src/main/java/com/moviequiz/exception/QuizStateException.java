package com.moviequiz.exception;

/**
 * Thrown when a submitted quiz answer refers to a question that has
 * already been answered, has expired, or was never issued.
 */
public class QuizStateException extends RuntimeException {
    public QuizStateException(String message) {
        super(message);
    }
}
