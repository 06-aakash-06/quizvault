package com.quizvault.gui;

import com.quizvault.filehandler.FileHandler;
import com.quizvault.filehandler.PasswordUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class LoginFrame extends JFrame {

    private final FileHandler fileHandler = new FileHandler();

    public LoginFrame() {
        super("Login - QuizVault");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 20));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(Theme.COLOR_BACKGROUND);

        JLabel titleLabel = new JLabel("Login", SwingConstants.CENTER);
        titleLabel.setFont(Theme.FONT_HEADING);
        titleLabel.setForeground(Theme.COLOR_TEXT_DARK);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JTextField userField = new JTextField(20);
        Theme.styleTextField(userField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Theme.COLOR_TEXT_DARK);
        JPasswordField passField = new JPasswordField(20);
        Theme.styleTextField(passField);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(userLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; formPanel.add(userField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(passLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(passField, gbc);
        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        JButton loginButton = new JButton("Login");
        Theme.styleModernButton(loginButton);
        JButton createAccountButton = new JButton("Create Account");
        Theme.styleModernButton(createAccountButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(createAccountButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);

        loginButton.addActionListener(e -> handleLogin(userField.getText(), new String(passField.getPassword())));
        createAccountButton.addActionListener(e -> handleCreateAccount());

        setVisible(true);
    }

    private void handleLogin(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Login Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            String role = fileHandler.authenticateUser(username, password);
            if (role != null) {
                JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                if (role.equals("Quizmaster")) {
                    new QuizSetupFrame();
                } else {
                    new ParticipantFrame();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading user data: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateAccount() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JRadioButton quizmasterRadio = new JRadioButton("Quizmaster");
        JRadioButton participantRadio = new JRadioButton("Participant");
        quizmasterRadio.setOpaque(false);
        participantRadio.setOpaque(false);
        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(quizmasterRadio);
        roleGroup.add(participantRadio);
        participantRadio.setSelected(true);

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(quizmasterRadio);
        panel.add(participantRadio);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = quizmasterRadio.isSelected() ? "Quizmaster" : "Participant";

            try {
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Username and password cannot be empty.", "Creation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (fileHandler.userExists(username)) {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.", "Creation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String hashedPassword = PasswordUtils.hashPassword(password);
                fileHandler.saveUser(username, hashedPassword, role);
                JOptionPane.showMessageDialog(this, "Account created successfully! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving user data: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}