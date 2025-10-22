package com.quizvault.quiz;

import com.quizvault.round.Round;
import com.quizvault.team.Team;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents the entire quiz, containing metadata, rounds, and teams. [cite: 149]
 */
public class Quiz implements Serializable {
    private static int quizCounter = 0; // Static counter for quiz IDs [cite: 176]
    private int quizId;

    private String quizName; 
    private String quizDate;
    private String quizmasterName; 
    private String organizingBody; 
    private ArrayList<Round> rounds; 
    private ArrayList<Team> teams; 
    private Scoreboard scoreboard; 

    /**
     * Constructor for the Quiz class. [cite: 158]
     */
    public Quiz(String quizName, String quizDate, String quizmasterName, String organizingBody) {
        this.quizId = ++quizCounter;
        this.quizName = quizName;
        this.quizDate = quizDate;
        this.quizmasterName = quizmasterName;
        this.organizingBody = organizingBody;
        this.rounds = new ArrayList<Round>();
        this.teams = new ArrayList<Team>(); 
    }
    
    // Getters
    public String getQuizName() { return quizName; }
    public String getQuizDate() { return quizDate; }
    public String getQuizmasterName() { return quizmasterName; }
    public String getOrganizingBody() { return organizingBody; }
    public ArrayList<Round> getRounds() { return rounds; }
    public ArrayList<Team> getTeams() { return teams; }
    public Scoreboard getScoreboard() { return scoreboard; }

    public void addRound(Round round) { 
        this.rounds.add(round);
    }

    public void addTeam(Team team) { 
        this.teams.add(team);
    }
    
    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
        this.scoreboard = new Scoreboard(this.teams);
    }
}