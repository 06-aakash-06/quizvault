package com.quizvault.exceptions;

/**
 * Custom exception thrown for invalid score values. [cite: 213]
 */
public class InvalidScoreException extends Exception {
    public InvalidScoreException(String message) {
        super(message);
    }
}