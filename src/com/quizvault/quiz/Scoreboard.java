package com.quizvault.quiz;

import com.quizvault.team.Team;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Manages and displays scores for a quiz. [cite: 153]
 */
public class Scoreboard {
    private ArrayList<Team> teams; 

    /**
     * Constructor for the Scoreboard. [cite: 162]
     * @param teams The list of teams participating.
     */
    public Scoreboard(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public void updateScore(String teamName, int score) { 
        // Score updating logic is handled directly in the GUI and Team objects
    }

    /**
     * Generates a sorted leaderboard of teams based on total score. [cite: 172]
     * @param teams The list of teams to sort.
     * @return A sorted list of teams.
     */
    public static ArrayList<Team> generateLeaderboard(ArrayList<Team> teams) { 
        // Sorts the list of teams in descending order of their total score.
        Collections.sort(teams, new Comparator<Team>() {
            @Override
            public int compare(Team t1, Team t2) {
                return Integer.compare(t2.getTotalScore(), t1.getTotalScore());
            }
        });
        return teams;
    }
}