package com.quizvault.exceptions;

/**
 * Custom exception thrown when a quiz is not found in the archive. [cite: 215]
 */
public class QuizNotFoundException extends Exception {
    public QuizNotFoundException(String message) {
        super(message);
    }
}