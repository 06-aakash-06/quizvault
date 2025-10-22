package com.quizvault.user;

import com.quizvault.team.Team;

/**
 * Represents a Quizmaster, who can create and conduct quizzes. [cite: 155]
 * Extends the User class. [cite: 181]
 */
public class Quizmaster extends User {

    public void createQuiz() { 
        // Logic to create a quiz would be handled by GUI and Quiz classes
    }

    public void conductQuiz() { 
        // Logic for conducting a quiz
    }

    public void updateScore(Team team, int score) { 
        // Logic to update a team's score
    }

    /**
     * Overridden method to view scores. A quizmaster has full access. [cite: 186, 187, 378]
     */
    @Override
    public void viewScores() { 
        System.out.println("Displaying scores with full access (questions, answers, scores).");
    }
}