package com.quizvault.exceptions;

/**
 * Custom exception thrown when a team cannot be found. [cite: 214]
 */
public class TeamNotFoundException extends Exception {
    public TeamNotFoundException(String message) {
        super(message); 
    }
}