package com.quizvault.gui;

import com.quizvault.quiz.Quiz;
import com.quizvault.quiz.Scoreboard;
import com.quizvault.round.Question;
import com.quizvault.round.Round;
import com.quizvault.team.Team;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;

/**
 * Redesigned frame to display the details of a past quiz from the archive.
 */
public class ArchiveViewerFrame extends JFrame {

    private Quiz quiz;

    public ArchiveViewerFrame(Quiz quiz) {
        super("Archive: " + quiz.getQuizName());
        this.quiz = quiz;

        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Top Panel: Metadata and Leaderboard
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        // Metadata
        JPanel metaPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        metaPanel.setOpaque(false);
        metaPanel.setBorder(new TitledBorder(null, "Quiz Details", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM));
        metaPanel.add(new JLabel("Date: " + quiz.getQuizDate()));
        metaPanel.add(new JLabel("Quizmaster: " + quiz.getQuizmasterName()));
        metaPanel.add(new JLabel("Organizer: " + quiz.getOrganizingBody()));
        topPanel.add(metaPanel, BorderLayout.NORTH);

        // Leaderboard
        ArrayList<Team> leaderboard = Scoreboard.generateLeaderboard(quiz.getTeams());
        String[] columns = {"Rank", "Team", "Score"};
        Object[][] data = new Object[leaderboard.size()][3];
        for (int i = 0; i < leaderboard.size(); i++) {
            data[i][0] = i + 1;
            data[i][1] = leaderboard.get(i).getTeamName();
            data[i][2] = leaderboard.get(i).getTotalScore();
        }
        JTable leaderboardTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(leaderboardTable);
        Theme.styleTable(leaderboardTable, scrollPane);
        scrollPane.setBorder(new TitledBorder(null, "Final Leaderboard", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, Theme.FONT_SUBHEADING, Theme.COLOR_TEXT_MEDIUM));
        topPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Questions and Answers in a Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(Theme.FONT_BODY);
        
        for (Round round : quiz.getRounds()) {
            JTextArea qaArea = new JTextArea();
            qaArea.setEditable(false);
            qaArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            Theme.styleTextField(qaArea);
            
            StringBuilder sb = new StringBuilder();
            sb.append("Round: ").append(round.getRoundName()).append("\n\n");
            int qNum = 1;
            for (Question q : round.getQuestions()) {
                sb.append("Q").append(qNum).append(": ").append(q.getQuestionText()).append("\n");
                sb.append("A: ").append(q.getAnswerText()).append("\n\n");
                qNum++;
            }
            qaArea.setText(sb.toString());
            qaArea.setCaretPosition(0); // Scroll to top
            
            JScrollPane roundScroll = new JScrollPane(qaArea);
            roundScroll.setBorder(null);
            tabbedPane.addTab(round.getRoundName(), roundScroll);
        }
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, tabbedPane);
        splitPane.setDividerLocation(350);
        splitPane.setBorder(null);

        mainPanel.add(splitPane, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }
}