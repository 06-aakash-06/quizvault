package com.quizvault.user;

/**
 * Abstract base class for all users. [cite: 154, 178]
 */
public abstract class User {
    protected String username; 

    /**
     * Abstract method for viewing scores, to be implemented by subclasses. [cite: 372]
     */
    public abstract void viewScores();
}