package com.quizvault.gui;

import com.quizvault.filehandler.FileHandler;
import com.quizvault.quiz.Quiz;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

/**
 * Redesigned frame for the participant to browse the quiz archive.
 */
public class ParticipantFrame extends JFrame {

    private FileHandler fileHandler;
    private JList<String> quizList;
    private DefaultListModel<String> listModel;
    private JButton viewDetailsButton;

    public ParticipantFrame() {
        super("Participant - Quiz Archive");
        fileHandler = new FileHandler();

        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Theme.COLOR_BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Quiz Archive", JLabel.CENTER);
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(Theme.COLOR_TEXT_DARK);
        mainPanel.add(title, BorderLayout.NORTH);

        // Quiz List
        listModel = new DefaultListModel<String>();
        quizList = new JList<String>(listModel);
        quizList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quizList.setFont(Theme.FONT_BODY);
        quizList.setFixedCellHeight(40);
        quizList.setSelectionBackground(Theme.COLOR_PRIMARY_LIGHT);
        quizList.setSelectionForeground(Theme.COLOR_WHITE);
        JScrollPane scrollPane = new JScrollPane(quizList);
        scrollPane.setBorder(BorderFactory.createLineBorder(Theme.COLOR_BORDER));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 0));
        bottomPanel.setOpaque(false);
        viewDetailsButton = new JButton("View Quiz Details");
        Theme.styleModernButton(viewDetailsButton);
        viewDetailsButton.setEnabled(false);
        
        JButton backButton = new JButton("Back to Main Menu");
        Theme.styleModernButton(backButton);
        
        bottomPanel.add(backButton, BorderLayout.WEST);
        bottomPanel.add(viewDetailsButton, BorderLayout.EAST);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        // Listeners
        quizList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                viewDetailsButton.setEnabled(quizList.getSelectedIndex() != -1);
            }
        });

        viewDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedQuizName = quizList.getSelectedValue();
                if (selectedQuizName != null) {
                    try {
                        Quiz quiz = fileHandler.loadQuiz(selectedQuizName);
                        new ArchiveViewerFrame(quiz);
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Error loading quiz: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MainMenuFrame();
                dispose();
            }
        });

        loadArchivedQuizzes();
        setVisible(true);
    }

    private void loadArchivedQuizzes() {
        List<String> quizNames = fileHandler.getArchivedQuizNames();
        for (String name : quizNames) {
            listModel.addElement(name);
        }
    }
}