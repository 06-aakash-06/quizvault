package com.quizvault.team;

import com.quizvault.exceptions.InvalidScoreException;
import java.util.HashMap;

public class Team implements Scorable {

    private String teamName;
    private int totalScore;
    private HashMap<Integer, Integer> roundScores;

    public Team(String teamName) {
        this.teamName = teamName;
        this.totalScore = 0;
        this.roundScores = new HashMap<>();
    }

    public String getTeamName() {
        return teamName;
    }

    // --- ADD THIS METHOD ---
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
    // -----------------------

    @Override
    public void addScore(int score) throws InvalidScoreException {
        if (score < 0) {
            throw new InvalidScoreException("Score to add must be non-negative.");
        }
        this.totalScore += score;
    }

    @Override
    public void deductScore(int score) throws InvalidScoreException {
        if (score < 0) {
            throw new InvalidScoreException("Score to deduct must be non-negative.");
        }
        this.totalScore -= score;
    }

    @Override
    public int getTotalScore() {
        return this.totalScore;
    }

    public void addRoundScore(int roundNum, int score) {
        int currentRoundScore = this.roundScores.getOrDefault(roundNum, 0);
        this.roundScores.put(roundNum, currentRoundScore + score);
    }

    public int getRoundScore(int roundNum) {
        return this.roundScores.getOrDefault(roundNum, 0);
    }
}