package com.quizvault.gui;

import com.quizvault.filehandler.FileHandler;
import com.quizvault.quiz.Quiz;
import com.quizvault.quiz.Scoreboard;
import com.quizvault.team.Team;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

/**
 * A redesigned dialog to display final quiz results.
 */
public class FinalResultsDialog extends JDialog {

    private Quiz quiz;

    public FinalResultsDialog(Frame owner, Quiz quiz) {
        super(owner, "Final Results: " + quiz.getQuizName(), true);
        this.quiz = quiz;
        
        setSize(600, 450);
        setLocationRelativeTo(owner);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBackground(Theme.COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        ArrayList<Team> leaderboard = Scoreboard.generateLeaderboard(quiz.getTeams());
        String winner = leaderboard.isEmpty() ? "N/A" : leaderboard.get(0).getTeamName();

        JLabel winnerLabel = new JLabel("Winner: " + winner, JLabel.CENTER);
        winnerLabel.setFont(Theme.FONT_HEADING);
        winnerLabel.setForeground(Theme.COLOR_ACCENT);
        winnerLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        // Leaderboard table
        DefaultTableModel model = new DefaultTableModel(new String[]{"Rank", "Team", "Final Score"}, 0);
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        Theme.styleTable(table, scrollPane);
        
        for (int i = 0; i < leaderboard.size(); i++) {
            Team team = leaderboard.get(i);
            model.addRow(new Object[]{i + 1, team.getTeamName(), team.getTotalScore()});
        }
        
        mainPanel.add(winnerLabel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        JButton exportCsvButton = new JButton("Export to CSV");
        Theme.styleModernButton(exportCsvButton);
        JButton closeButton = new JButton("Return to Main Menu");
        Theme.styleModernButton(closeButton);
        
        buttonPanel.add(exportCsvButton);
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        exportCsvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new FileHandler().exportResults(quiz);
                    JOptionPane.showMessageDialog(null, "Results exported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(null, "Error exporting results: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                owner.dispose();
                dispose();
                new MainMenuFrame();
            }
        });
        
        add(mainPanel);
        setVisible(true);
    }
}