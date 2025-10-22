package com.quizvault.gui;

import com.quizvault.filehandler.FileHandler;
import com.quizvault.quiz.Quiz;
import com.quizvault.round.NormalRound;
import com.quizvault.round.Question;
import com.quizvault.team.Team;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuizSetupFrame extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private Quiz quiz;
    private ArrayList<Team> teams = new ArrayList<>();
    private DefaultTableModel teamsTableModel;
    private JTable teamsTable;
    private ArrayList<NormalRound> rounds = new ArrayList<>();
    private DefaultTableModel roundsTableModel;
    private JTable roundsTable;
    private DefaultTableModel questionsTableModel;
    private JTable questionsTable;
    private TitledBorder questionsBorder;

    public QuizSetupFrame() {
        super("Quizmaster - Create New Quiz");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        mainPanel.setBackground(Theme.COLOR_BACKGROUND);
        mainPanel.add(createQuizDetailsPanel(), "Details");
        mainPanel.add(createAddTeamsPanel(), "Teams");
        mainPanel.add(createAddRoundsPanel(), "Rounds");
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createQuizDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBackground(Theme.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JLabel title = new JLabel("Step 1: Enter Quiz Details", JLabel.CENTER);
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.EAST;
        JLabel quizNameLabel = new JLabel("Quiz Name:");
        quizNameLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JLabel quizDateLabel = new JLabel("Quiz Date:");
        quizDateLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JLabel quizmasterNameLabel = new JLabel("Quizmaster Name:");
        quizmasterNameLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JLabel organizingBodyLabel = new JLabel("Organizing Body:");
        organizingBodyLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JTextField quizNameField = new JTextField(30);
        Theme.styleTextField(quizNameField);
        JTextField quizDateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), 30);
        Theme.styleTextField(quizDateField);
        JTextField quizmasterNameField = new JTextField(30);
        Theme.styleTextField(quizmasterNameField);
        JTextField organizingBodyField = new JTextField(30);
        Theme.styleTextField(organizingBodyField);
        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(quizNameLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; formPanel.add(quizNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(quizDateLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; formPanel.add(quizDateField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(quizmasterNameLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; formPanel.add(quizmasterNameField, gbc);
        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(organizingBodyLabel, gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; formPanel.add(organizingBodyField, gbc);
        panel.add(formPanel, BorderLayout.CENTER);
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        navPanel.setOpaque(false);
        JButton createQuizButton = new JButton("Next: Add Teams");
        Theme.styleModernButton(createQuizButton);
        createQuizButton.addActionListener(e -> {
            String quizName = quizNameField.getText();
            if (quizName.trim().isEmpty()) { JOptionPane.showMessageDialog(null, "Quiz Name cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
            quiz = new Quiz(quizName, quizDateField.getText(), quizmasterNameField.getText(), organizingBodyField.getText());
            cardLayout.show(mainPanel, "Teams");
        });
        navPanel.add(createQuizButton);
        panel.add(navPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createAddTeamsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBackground(Theme.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JLabel title = new JLabel("Step 2: Add Teams", JLabel.CENTER);
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);
        teamsTableModel = new DefaultTableModel(new Object[]{"Team Name"}, 0);
        teamsTable = new JTable(teamsTableModel);
        JScrollPane scrollPane = new JScrollPane(teamsTable);
        Theme.styleTable(teamsTable, scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setOpaque(false);
        JPanel managementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        managementPanel.setOpaque(false);
        JButton manageTeamsButton = new JButton("Bulk Add...");
        Theme.styleModernButton(manageTeamsButton);
        manageTeamsButton.addActionListener(e -> showTeamManagementDialog());
        managementPanel.add(manageTeamsButton);
        JButton editTeamButton = new JButton("Edit Selected");
        Theme.styleModernButton(editTeamButton);
        managementPanel.add(editTeamButton);
        JButton deleteTeamButton = new JButton("Delete Selected");
        Theme.styleModernButton(deleteTeamButton);
        managementPanel.add(deleteTeamButton);
        editTeamButton.addActionListener(e -> {
            int selectedRow = teamsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String currentName = (String) teamsTableModel.getValueAt(selectedRow, 0);
                String newName = JOptionPane.showInputDialog(this, "Enter new name for the team:", currentName);
                if (newName != null && !newName.trim().isEmpty()) {
                    teams.get(selectedRow).setTeamName(newName.trim());
                    teamsTableModel.setValueAt(newName.trim(), selectedRow, 0);
                }
            } else { JOptionPane.showMessageDialog(this, "Please select a team to edit.", "No Selection", JOptionPane.WARNING_MESSAGE); }
        });
        deleteTeamButton.addActionListener(e -> {
            int selectedRow = teamsTable.getSelectedRow();
            if (selectedRow >= 0) {
                if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this team?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    teams.remove(selectedRow);
                    teamsTableModel.removeRow(selectedRow);
                }
            } else { JOptionPane.showMessageDialog(this, "Please select a team to delete.", "No Selection", JOptionPane.WARNING_MESSAGE); }
        });
        JPanel navPanel = new JPanel(new BorderLayout(10, 0));
        navPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        Theme.styleModernButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Details"));
        JButton uploadTeamsButton = new JButton("Upload (CSV)");
        Theme.styleModernButton(uploadTeamsButton);
        uploadTeamsButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                try {
                    ArrayList<Team> loadedTeams = new FileHandler().loadTeams(chooser.getSelectedFile());
                    teams.addAll(loadedTeams);
                    for (Team team : loadedTeams) teamsTableModel.addRow(new Object[]{team.getTeamName()});
                } catch (IOException ex) { JOptionPane.showMessageDialog(null, "Error loading teams file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE); }
            }
        });
        JButton nextButton = new JButton("Next: Add Rounds");
        Theme.styleModernButton(nextButton);
        nextButton.addActionListener(e -> {
            if (teams.isEmpty()) { JOptionPane.showMessageDialog(null, "Please add at least one team.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
            quiz.setTeams(teams);
            cardLayout.show(mainPanel, "Rounds");
        });
        JPanel rightNav = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightNav.setOpaque(false);
        rightNav.add(uploadTeamsButton);
        rightNav.add(nextButton);
        navPanel.add(backButton, BorderLayout.WEST);
        navPanel.add(rightNav, BorderLayout.EAST);
        bottomPanel.add(managementPanel, BorderLayout.NORTH);
        bottomPanel.add(navPanel, BorderLayout.SOUTH);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }
    
    private void showTeamManagementDialog() {
        JTextArea bulkAddArea = new JTextArea(10, 30);
        bulkAddArea.setToolTipText("Enter one team name per line");
        Theme.styleTextField(bulkAddArea);
        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(new JLabel("Enter all team names below, one per line:"), BorderLayout.NORTH);
        dialogPanel.add(new JScrollPane(bulkAddArea), BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Bulk Add Teams", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String[] lines = bulkAddArea.getText().split("\\n");
            int teamsAdded = 0;
            for (String teamName : lines) {
                if (!teamName.trim().isEmpty()) {
                    teams.add(new Team(teamName.trim()));
                    teamsTableModel.addRow(new Object[]{teamName.trim()});
                    teamsAdded++;
                }
            }
            JOptionPane.showMessageDialog(this, teamsAdded + " teams were added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private JPanel createAddRoundsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 20));
        panel.setBackground(Theme.COLOR_BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));
        JLabel title = new JLabel("Step 3: Add Rounds and Questions", JLabel.CENTER);
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.COLOR_TEXT_DARK);
        panel.add(title, BorderLayout.NORTH);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setOpaque(false);
        splitPane.setBorder(null);

        JPanel roundsPanel = new JPanel(new BorderLayout(10, 10));
        roundsPanel.setOpaque(false);
        roundsPanel.setBorder(new TitledBorder(null, "Rounds", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM));
        roundsTableModel = new DefaultTableModel(new Object[]{"Round Name"}, 0);
        roundsTable = new JTable(roundsTableModel);
        Theme.styleTable(roundsTable, new JScrollPane(roundsTable));
        roundsPanel.add(new JScrollPane(roundsTable), BorderLayout.CENTER);
        
        JPanel roundManagementPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roundManagementPanel.setOpaque(false);
        JButton bulkAddRoundsButton = new JButton("Bulk Add...");
        Theme.styleModernButton(bulkAddRoundsButton);
        bulkAddRoundsButton.addActionListener(e -> showRoundManagementDialog());
        JButton deleteRoundButton = new JButton("Delete Selected");
        Theme.styleModernButton(deleteRoundButton);
        deleteRoundButton.addActionListener(e -> deleteSelectedRounds());
        roundManagementPanel.add(bulkAddRoundsButton);
        roundManagementPanel.add(deleteRoundButton);
        roundsPanel.add(roundManagementPanel, BorderLayout.SOUTH);

        JPanel questionsPanel = new JPanel(new BorderLayout(10, 10));
        questionsPanel.setOpaque(false);
        questionsBorder = new TitledBorder(null, "Questions for Selected Round", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM);
        questionsPanel.setBorder(questionsBorder);
        questionsTableModel = new DefaultTableModel(new Object[]{"Question", "Answer"}, 0);
        questionsTable = new JTable(questionsTableModel);
        Theme.styleTable(questionsTable, new JScrollPane(questionsTable));
        questionsPanel.add(new JScrollPane(questionsTable), BorderLayout.CENTER);
        
        JPanel questionInputContainer = new JPanel(new BorderLayout(5, 5));
        questionInputContainer.setOpaque(false);
        JPanel manualAddPanel = new JPanel(new GridBagLayout());
        manualAddPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        JLabel questionLabel = new JLabel("Question:");
        questionLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JTextField questionField = new JTextField(20);
        Theme.styleTextField(questionField);
        JTextField answerField = new JTextField(20);
        Theme.styleTextField(answerField);
        JButton addQuestionButton = new JButton("Add Question");
        Theme.styleModernButton(addQuestionButton);
        gbc.gridx=0; gbc.gridy=0; gbc.anchor = GridBagConstraints.EAST; manualAddPanel.add(questionLabel, gbc);
        gbc.gridx=1; gbc.gridy=0; gbc.anchor = GridBagConstraints.WEST; manualAddPanel.add(questionField, gbc);
        gbc.gridx=0; gbc.gridy=1; gbc.anchor = GridBagConstraints.EAST; manualAddPanel.add(answerLabel, gbc);
        gbc.gridx=1; gbc.gridy=1; gbc.anchor = GridBagConstraints.WEST; manualAddPanel.add(answerField, gbc);
        gbc.gridx=1; gbc.gridy=2; gbc.anchor = GridBagConstraints.EAST; manualAddPanel.add(addQuestionButton, gbc);
        JPanel questionManagementPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        questionManagementPanel.setOpaque(false);
        JButton importCSVButton = new JButton("Import from CSV...");
        Theme.styleModernButton(importCSVButton);
        importCSVButton.addActionListener(e -> importQuestionsFromCSV());
        JButton deleteQuestionButton = new JButton("Delete Selected Question");
        Theme.styleModernButton(deleteQuestionButton);
        deleteQuestionButton.addActionListener(e -> deleteSelectedQuestion());
        questionManagementPanel.add(deleteQuestionButton);
        questionManagementPanel.add(importCSVButton);
        questionInputContainer.add(manualAddPanel, BorderLayout.NORTH);
        questionInputContainer.add(questionManagementPanel, BorderLayout.CENTER);
        questionsPanel.add(questionInputContainer, BorderLayout.SOUTH);

        splitPane.setLeftComponent(roundsPanel);
        splitPane.setRightComponent(questionsPanel);
        panel.add(splitPane, BorderLayout.CENTER);

        roundsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateQuestionPanel();
        });
        addQuestionButton.addActionListener(e -> {
            int selectedRoundIndex = roundsTable.getSelectedRow();
            if (selectedRoundIndex == -1) { JOptionPane.showMessageDialog(this, "Please select a round first!", "Error", JOptionPane.ERROR_MESSAGE); return; }
            String qText = questionField.getText();
            String aText = answerField.getText();
            if (!qText.trim().isEmpty() && !aText.trim().isEmpty()) {
                rounds.get(selectedRoundIndex).addQuestion(new Question(qText, aText));
                updateQuestionPanel();
                questionField.setText("");
                answerField.setText("");
                questionField.requestFocusInWindow();
            }
        });
        
        JPanel navPanel = new JPanel(new BorderLayout(10, 0));
        navPanel.setOpaque(false);
        JButton backButton = new JButton("Back");
        Theme.styleModernButton(backButton);
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Teams"));
        JButton startQuizButton = new JButton("Save and Start Quiz");
        Theme.styleModernButton(startQuizButton);
        startQuizButton.addActionListener(e -> {
            if (rounds.isEmpty() || rounds.stream().anyMatch(r -> r.getQuestions().isEmpty())) { JOptionPane.showMessageDialog(this, "Please add at least one round and ensure every round has at least one question.", "Validation Error", JOptionPane.ERROR_MESSAGE); return; }
            rounds.forEach(quiz::addRound);
            try { new FileHandler().saveQuiz(quiz); } catch (IOException ex) { JOptionPane.showMessageDialog(null, "Error saving quiz: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE); return; }
            new LiveQuizFrame(quiz);
            dispose();
        });
        navPanel.add(backButton, BorderLayout.WEST);
        navPanel.add(startQuizButton, BorderLayout.EAST);
        panel.add(navPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void showRoundManagementDialog() {
        JTextArea bulkAddArea = new JTextArea(10, 30);
        bulkAddArea.setToolTipText("Enter one round name per line");
        Theme.styleTextField(bulkAddArea);
        JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
        dialogPanel.add(new JLabel("Enter all round names below, one per line:"), BorderLayout.NORTH);
        dialogPanel.add(new JScrollPane(bulkAddArea), BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(this, dialogPanel, "Bulk Add Rounds", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String[] lines = bulkAddArea.getText().split("\\n");
            int roundsAdded = 0;
            for (String roundName : lines) {
                if (!roundName.trim().isEmpty()) {
                    rounds.add(new NormalRound(roundName.trim()));
                    roundsTableModel.addRow(new Object[]{roundName.trim()});
                    roundsAdded++;
                }
            }
            JOptionPane.showMessageDialog(this, roundsAdded + " rounds were added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateQuestionPanel() {
        int selectedRoundIndex = roundsTable.getSelectedRow();
        questionsTableModel.setRowCount(0);
        if (selectedRoundIndex != -1) {
            NormalRound selectedRound = rounds.get(selectedRoundIndex);
            questionsBorder.setTitle("Questions for '" + selectedRound.getRoundName() + "'");
            questionsTable.getParent().getParent().repaint();
            for (Question q : selectedRound.getQuestions()) {
                questionsTableModel.addRow(new Object[]{q.getQuestionText(), q.getAnswerText()});
            }
        } else {
            questionsBorder.setTitle("Questions for Selected Round");
            questionsTable.getParent().getParent().repaint();
        }
    }
    
    private void importQuestionsFromCSV() {
        int selectedRoundIndex = roundsTable.getSelectedRow();
        if (selectedRoundIndex == -1) { JOptionPane.showMessageDialog(this, "Please select a round first before importing questions.", "No Round Selected", JOptionPane.WARNING_MESSAGE); return; }
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files (*.csv)", "csv"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                NormalRound selectedRound = rounds.get(selectedRoundIndex);
                int questionsAdded = 0;
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length >= 2) {
                        String qText = values[0].trim();
                        String aText = values[1].trim();
                        if (!qText.isEmpty() && !aText.isEmpty()) {
                            selectedRound.addQuestion(new Question(qText, aText));
                            questionsAdded++;
                        }
                    }
                }
                updateQuestionPanel();
                JOptionPane.showMessageDialog(this, questionsAdded + " questions were successfully imported into '" + selectedRound.getRoundName() + "'.", "Import Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) { JOptionPane.showMessageDialog(this, "Failed to read the CSV file.\nError: " + e.getMessage(), "Import Error", JOptionPane.ERROR_MESSAGE); e.printStackTrace(); }
        }
    }

    private void deleteSelectedRounds() {
        int[] selectedRows = roundsTable.getSelectedRows();
        if (selectedRows.length > 0) {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the selected round(s)?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                // To safely remove from the model and the list, we iterate backwards
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    int modelRow = selectedRows[i];
                    rounds.remove(modelRow);
                    roundsTableModel.removeRow(modelRow);
                }
                updateQuestionPanel();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select at least one round to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteSelectedQuestion() {
        int selectedRoundIndex = roundsTable.getSelectedRow();
        int selectedQuestionIndex = questionsTable.getSelectedRow();
        if (selectedRoundIndex == -1) { JOptionPane.showMessageDialog(this, "Please select the parent round first.", "No Round Selected", JOptionPane.WARNING_MESSAGE); return; }
        if (selectedQuestionIndex == -1) { JOptionPane.showMessageDialog(this, "Please select a question to delete.", "No Question Selected", JOptionPane.WARNING_MESSAGE); return; }
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this question?", "Confirm Deletion", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            rounds.get(selectedRoundIndex).getQuestions().remove(selectedQuestionIndex);
            questionsTableModel.removeRow(selectedQuestionIndex);
        }
    }
}