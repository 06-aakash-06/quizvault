package com.quizvault.round;

/**
 * Represents a single quiz question with its answer. [cite: 151]
 */
public class Question {
    private String questionText; 
    private String answerText; 
    private int questionNumber; 

    /**
     * Constructor for the Question class. [cite: 160]
     * @param questionText The text of the question.
     * @param answerText The text of the answer.
     */
    public Question(String questionText, String answerText) {
        this.questionText = questionText;
        this.answerText = answerText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getAnswerText() {
        return answerText;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }
}