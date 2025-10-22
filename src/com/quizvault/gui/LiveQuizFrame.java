package com.quizvault.gui;

import com.quizvault.exceptions.InvalidScoreException;
import com.quizvault.quiz.Quiz;
import com.quizvault.quiz.Scoreboard;
import com.quizvault.round.Question;
import com.quizvault.round.Round;
import com.quizvault.team.Team;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class LiveQuizFrame extends JFrame {

    private Quiz quiz;
    private int currentRoundIndex = 0;
    private int currentQuestionIndex = 0;
    private Team selectedTeam = null;
    private JButton selectedTeamButton = null;
    private HashMap<Team, JButton> teamButtonsMap = new HashMap<>();

    private JLabel infoLabel;
    private JTextArea questionArea;
    private JTextArea answerArea;
    private JTable scoreboardTable;
    private DefaultTableModel scoreboardModel;
    private JButton nextQuestionButton;
    private JButton endRoundButton;

    private JButton undoButton;
    private Team lastScoredTeam = null;
    private int lastScoreValue = 0;
    private JTextField customScoreField;

    public LiveQuizFrame(Quiz quiz) {
        super("Live Quiz: " + quiz.getQuizName());
        this.quiz = quiz;
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Theme.COLOR_BACKGROUND);

        infoLabel = new JLabel("Loading...", JLabel.CENTER);
        infoLabel.setFont(Theme.FONT_HEADING);
        infoLabel.setForeground(Theme.COLOR_TEXT_DARK);
        mainPanel.add(infoLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setOpaque(false);
        questionArea = new JTextArea("Questions for this round need to be added or loaded.");
        questionArea.setFont(new Font("Arial", Font.PLAIN, 28));
        questionArea.setWrapStyleWord(true);
        questionArea.setLineWrap(true);
        questionArea.setEditable(false);
        Theme.styleTextField(questionArea);
        answerArea = new JTextArea();
        answerArea.setFont(new Font("Arial", Font.ITALIC, 22));
        answerArea.setWrapStyleWord(true);
        answerArea.setLineWrap(true);
        answerArea.setEditable(false);
        answerArea.setPreferredSize(new Dimension(0, 150));
        Theme.styleTextField(answerArea);
        TitledBorder questionBorder = new TitledBorder(null, "Question", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM);
        JPanel questionPanel = new JPanel(new BorderLayout(10,10));
        questionPanel.setBorder(questionBorder);
        questionPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        TitledBorder answerBorder = new TitledBorder(null, "Answer", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM);
        JPanel answerPanel = new JPanel(new BorderLayout(10,10));
        answerPanel.setBorder(answerBorder);
        answerPanel.add(new JScrollPane(answerArea), BorderLayout.CENTER);
        JSplitPane questionSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, questionPanel, answerPanel);
        questionSplit.setResizeWeight(0.75);
        questionSplit.setBorder(null);
        centerPanel.add(questionSplit, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel rightPanel = new JPanel(new BorderLayout(10, 15));
        rightPanel.setPreferredSize(new Dimension(450, 0));
        rightPanel.setOpaque(false);
        createScoringPanel(rightPanel); 

        scoreboardModel = new DefaultTableModel(new String[]{"Team", "Round", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreboardTable = new JTable(scoreboardModel);
        JScrollPane scoreboardScrollPane = new JScrollPane(scoreboardTable);
        Theme.styleTable(scoreboardTable, scoreboardScrollPane);
        rightPanel.add(scoreboardScrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        navPanel.setOpaque(false);
        nextQuestionButton = new JButton("Next Question");
        endRoundButton = new JButton("End Round");
        Theme.styleModernButton(nextQuestionButton);
        Theme.styleModernButton(endRoundButton);
        endRoundButton.setEnabled(false);
        navPanel.add(nextQuestionButton);
        navPanel.add(endRoundButton);
        mainPanel.add(navPanel, BorderLayout.SOUTH);

        add(mainPanel);
        nextQuestionButton.addActionListener(e -> loadNextQuestion());
        endRoundButton.addActionListener(e -> endRound());
        updateScoreboard();
        loadNextQuestion();
        setVisible(true);
    }
    
    private void createScoringPanel(JPanel rightPanel) {
        TitledBorder assignScoreBorder = new TitledBorder(null, "Assign Score", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM);
        JPanel teamSelectionPanel = new JPanel(new BorderLayout(10, 10));
        teamSelectionPanel.setBorder(assignScoreBorder);
        teamSelectionPanel.setOpaque(false);

        JPanel buttonsGrid = new JPanel(new GridLayout(0, 2, 8, 8)); 
        buttonsGrid.setOpaque(false);
        for (final Team team : quiz.getTeams()) {
            // --- THIS IS THE CHANGE: REMOVED THE SCORE FROM THE BUTTON TEXT ---
            final JButton teamButton = new JButton(team.getTeamName());
            teamButton.setFont(Theme.FONT_BUTTON);
            teamButton.setBackground(Theme.COLOR_WHITE);
            teamButton.setForeground(Theme.COLOR_TEXT_DARK);
            teamButton.setBorder(Theme.BORDER_DEFAULT);
            teamButton.setFocusPainted(false);
            teamButton.addActionListener(e -> {
                selectedTeam = team;
                if (selectedTeamButton != null) {
                    selectedTeamButton.setBorder(Theme.BORDER_DEFAULT);
                }
                selectedTeamButton = teamButton;
                selectedTeamButton.setBorder(Theme.BORDER_SELECTED);
            });
            buttonsGrid.add(teamButton);
            teamButtonsMap.put(team, teamButton);
        }
        
        JPanel allActionsPanel = new JPanel();
        allActionsPanel.setLayout(new BoxLayout(allActionsPanel, BoxLayout.Y_AXIS));
        allActionsPanel.setOpaque(false);

        JPanel quickScorePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        quickScorePanel.setOpaque(false);
        int[] scores = {10, 5, -5, -10};
        for (int score : scores) {
            JButton scoreButton = new JButton((score > 0 ? "+" : "") + score);
            Theme.styleModernButton(scoreButton);
            scoreButton.addActionListener(e -> quickUpdateScore(score));
            quickScorePanel.add(scoreButton);
        }

        JPanel customActionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        customActionsPanel.setOpaque(false);

        JLabel customLabel = new JLabel("Custom:");
        customLabel.setForeground(Theme.COLOR_TEXT_DARK);

        customScoreField = new JTextField(4);
        Theme.styleTextField(customScoreField);

        JButton addCustomScoreButton = new JButton("Add");
        Theme.styleModernButton(addCustomScoreButton);
        addCustomScoreButton.addActionListener(e -> addCustomScore());
        
        undoButton = new JButton("Undo");
        Theme.styleModernButton(undoButton);
        undoButton.setEnabled(false);
        undoButton.addActionListener(e -> undoLastScore());
        
        customActionsPanel.add(customLabel);
        customActionsPanel.add(customScoreField);
        customActionsPanel.add(addCustomScoreButton);
        customActionsPanel.add(undoButton);
        
        allActionsPanel.add(quickScorePanel);
        allActionsPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        allActionsPanel.add(customActionsPanel);
        
        teamSelectionPanel.add(buttonsGrid, BorderLayout.CENTER);
        teamSelectionPanel.add(allActionsPanel, BorderLayout.SOUTH);
        rightPanel.add(teamSelectionPanel, BorderLayout.NORTH);
    }

    private void addCustomScore() {
        if (selectedTeam == null) {
            JOptionPane.showMessageDialog(this, "Please select a team first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int score = Integer.parseInt(customScoreField.getText());
            quickUpdateScore(score);
            customScoreField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the custom score.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void quickUpdateScore(int score) {
        if (selectedTeam == null) {
            JOptionPane.showMessageDialog(this, "Please select a team first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            lastScoredTeam = selectedTeam;
            lastScoreValue = score;
            
            if (score >= 0) selectedTeam.addScore(score);
            else selectedTeam.deductScore(Math.abs(score));
            
            selectedTeam.addRoundScore(currentRoundIndex + 1, score);
            updateScoreboard();
            undoButton.setEnabled(true);
        } catch (InvalidScoreException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void undoLastScore() {
        if (lastScoredTeam == null) return;
        try {
            int scoreToReverse = -lastScoreValue;
            if (scoreToReverse >= 0) lastScoredTeam.addScore(scoreToReverse);
            else lastScoredTeam.deductScore(Math.abs(scoreToReverse));
            lastScoredTeam.addRoundScore(currentRoundIndex + 1, scoreToReverse);
            lastScoredTeam = null;
            lastScoreValue = 0;
            undoButton.setEnabled(false);
            updateScoreboard();
        } catch (InvalidScoreException e) {
            JOptionPane.showMessageDialog(this, "Error during undo: " + e.getMessage(), "Undo Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadNextQuestion() {
        if (currentRoundIndex >= quiz.getRounds().size()) { endQuiz(); return; }
        Round round = quiz.getRounds().get(currentRoundIndex);
        ArrayList<Question> questions = round.getQuestions();
        if (questions.isEmpty() || currentQuestionIndex >= questions.size()) {
            infoLabel.setText(round.getRoundName() + " | No more questions");
            questionArea.setText("End of questions for this round.");
            answerArea.setText("");
            nextQuestionButton.setEnabled(false);
            endRoundButton.setEnabled(true);
            return;
        }
        Question q = questions.get(currentQuestionIndex);
        infoLabel.setText(round.getRoundName() + "  |  Question " + (currentQuestionIndex + 1));
        questionArea.setText(q.getQuestionText());
        answerArea.setText(q.getAnswerText());
        currentQuestionIndex++;
        if (currentQuestionIndex >= questions.size()) {
            nextQuestionButton.setEnabled(false);
            endRoundButton.setEnabled(true);
        }
    }

    private void endRound() {
        int choice = JOptionPane.showConfirmDialog(this, "End of Round " + (currentRoundIndex + 1) + ". Proceed to the next round?", "Round Complete", JOptionPane.OK_CANCEL_OPTION);
        if (choice == JOptionPane.OK_OPTION) {
            currentRoundIndex++;
            currentQuestionIndex = 0;
            nextQuestionButton.setEnabled(true);
            endRoundButton.setEnabled(false);
            if (currentRoundIndex >= quiz.getRounds().size()) {
                endQuiz();
            } else {
                loadNextQuestion();
            }
        }
    }

    private void endQuiz() {
        nextQuestionButton.setEnabled(false);
        endRoundButton.setEnabled(false);
        infoLabel.setText("Quiz Finished!");
        questionArea.setText("The quiz has concluded. Please see the final results.");
        answerArea.setText("");
        new FinalResultsDialog(this, quiz);
    }

    private void updateScoreboard() {
        scoreboardModel.setRowCount(0);
        ArrayList<Team> sortedTeams = Scoreboard.generateLeaderboard(new ArrayList<>(quiz.getTeams()));
        for (Team team : sortedTeams) {
            Object[] row = new Object[]{ team.getTeamName(), team.getRoundScore(currentRoundIndex + 1), team.getTotalScore() };
            scoreboardModel.addRow(row);
            // --- THIS IS THE OTHER CHANGE: NO LONGER UPDATING BUTTON TEXT WITH SCORE ---
            // JButton buttonToUpdate = teamButtonsMap.get(team);
            // if (buttonToUpdate != null) {
            //     buttonToUpdate.setText(team.getTeamName() + " (" + team.getTotalScore() + ")");
            // }
        }
    }
}