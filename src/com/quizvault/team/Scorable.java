package com.quizvault.team;

import com.quizvault.exceptions.InvalidScoreException;

/**
 * Interface for objects that can be scored. [cite: 204]
 */
public interface Scorable {
    void addScore(int score) throws InvalidScoreException; 
    void deductScore(int score) throws InvalidScoreException; 
    int getTotalScore(); 
}