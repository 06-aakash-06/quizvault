package com.quizvault.user;

/**
 * Represents a Participant, who can view live scores and archives. [cite: 156]
 * Extends the User class. [cite: 183]
 */
public class Participant extends User {

    /**
     * Overridden method to view scores. A participant has restricted access. [cite: 186, 188, 381]
     */
    @Override
    public void viewScores() { 
        System.out.println("Displaying scores with restricted access (scores only).");
    }
}