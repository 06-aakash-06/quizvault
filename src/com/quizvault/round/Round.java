package com.quizvault.round;

import java.util.ArrayList;

public abstract class Round {
    protected String roundName;
    protected ArrayList<Question> questions; 

    public Round(String roundName) {
        this.roundName = roundName;
        this.questions = new ArrayList<>();
    }

    public String getRoundName() {
        return roundName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }
  

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void addQuestion(Question q) { 
        this.questions.add(q);
    }

    public abstract void displayRound();
}