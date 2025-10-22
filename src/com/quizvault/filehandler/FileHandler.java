package com.quizvault.filehandler;

import com.quizvault.quiz.Quiz;
import com.quizvault.quiz.Scoreboard;
import com.quizvault.round.NormalRound;
import com.quizvault.round.Question;
import com.quizvault.round.Round;
import com.quizvault.team.Team;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String ARCHIVE_DIR = "archives/";
    private static final String EXPORTS_DIR = "exports/";
    private static final String USERS_FILE = "users.csv"; // File for user data
    private static final String TEAMS_KEY = "#Teams";
    private static final String ROUNDS_KEY = "#Rounds";

    public FileHandler() {
        new File(ARCHIVE_DIR).mkdirs();
        new File(EXPORTS_DIR).mkdirs();
        try {
            new File(USERS_FILE).createNewFile(); // Ensure users.csv exists
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- NEW METHODS FOR USER AUTHENTICATION ---

    public String authenticateUser(String username, String password) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3 && parts[0].equals(username)) {
                    String storedHash = parts[1];
                    String role = parts[2];
                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        return role; // Authentication successful
                    }
                }
            }
        }
        return null; // Authentication failed
    }

    public boolean userExists(String username) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 0 && parts[0].equals(username)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void saveUser(String username, String hashedPassword, String role) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(username + "," + hashedPassword + "," + role + "\n");
        }
    }

    // --- EXISTING METHODS (UNCHANGED) ---

    public void saveQuiz(Quiz quiz) throws IOException {
        String fileName = ARCHIVE_DIR + quiz.getQuizName().replaceAll("\\s+", "_") + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("QUIZ_NAME:" + quiz.getQuizName() + "\n");
            writer.write("QUIZ_DATE:" + quiz.getQuizDate() + "\n");
            writer.write("QUIZMASTER:" + quiz.getQuizmasterName() + "\n");
            writer.write("ORGANIZER:" + quiz.getOrganizingBody() + "\n");
            writer.write(TEAMS_KEY + "\n");
            for (Team team : quiz.getTeams()) {
                writer.write("TEAM:" + team.getTeamName() + "\n");
            }
            writer.write(ROUNDS_KEY + "\n");
            for (Round round : quiz.getRounds()) {
                writer.write("ROUND_START\n");
                writer.write("ROUND_NAME:" + round.getRoundName() + "\n");
                for (Question question : round.getQuestions()) {
                    writer.write("QUESTION:" + question.getQuestionText() + "|" + question.getAnswerText() + "\n");
                }
                writer.write("ROUND_END\n");
            }
        }
    }

    public Quiz loadQuiz(String quizName) throws IOException {
        String fileName = ARCHIVE_DIR + quizName.replaceAll("\\s+", "_") + ".txt";
        Quiz quiz = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String name = reader.readLine().split(":")[1];
            String date = reader.readLine().split(":")[1];
            String master = reader.readLine().split(":")[1];
            String org = reader.readLine().split(":")[1];
            quiz = new Quiz(name, date, master, org);
            ArrayList<Team> teams = new ArrayList<>();
            ArrayList<Round> rounds = new ArrayList<>();
            Round currentRound = null;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("TEAM:")) {
                    teams.add(new Team(line.split(":")[1]));
                } else if (line.equals("ROUND_START")) {
                    String roundNameLine = reader.readLine();
                    currentRound = new NormalRound(roundNameLine.split(":")[1]);
                } else if (line.startsWith("QUESTION:") && currentRound != null) {
                    String[] parts = line.substring("QUESTION:".length()).split("\\|");
                    if (parts.length == 2) {
                        currentRound.addQuestion(new Question(parts[0], parts[1]));
                    }
                } else if (line.equals("ROUND_END") && currentRound != null) {
                    rounds.add(currentRound);
                    currentRound = null;
                }
            }
            quiz.setTeams(teams);
            for(Round r : rounds) quiz.addRound(r);
        }
        return quiz;
    }

    public List<String> getArchivedQuizNames() {
        File dir = new File(ARCHIVE_DIR);
        File[] files = dir.listFiles();
        ArrayList<String> quizNames = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".txt")) {
                    quizNames.add(file.getName().replace(".txt", "").replaceAll("_", " "));
                }
            }
        }
        return quizNames;
    }

    public void exportResults(Quiz quiz) throws IOException {
        String fileName = EXPORTS_DIR + quiz.getQuizName().replaceAll("\\s+", "_") + "_results.csv";
        ArrayList<Team> leaderboard = Scoreboard.generateLeaderboard(quiz.getTeams());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write("Rank,Team Name,Total Score\n");
            int rank = 1;
            for (Team team : leaderboard) {
                writer.write(rank + "," + team.getTeamName() + "," + team.getTotalScore() + "\n");
                rank++;
            }
        }
    }

    public ArrayList<Team> loadTeams(File file) throws IOException {
        ArrayList<Team> teams = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    teams.add(new Team(line.trim()));
                }
            }
        }
        return teams;
    }
}