package model;

import java.util.*;

public class GameState {
    private Quiz quiz;
    private int currentQuestionIndex = 0;
    private Map<String, Team> teams = new HashMap<>();

    public GameState(Quiz quiz) {
        this.quiz = quiz;
    }

    // Gerir equipas
    public void addTeam(Team team) {
        teams.put(team.getName(), team);
    }

    public Team getTeam(String name) {
        return teams.get(name);
    }

    public Collection<Team> getAllTeams() {
        return teams.values();
    }

    // Gerir perguntas
    public Question getCurrentQuestion() {
        if (currentQuestionIndex < quiz.getQuestions().size())
            return quiz.getQuestions().get(currentQuestionIndex);
        return null;
    }

    public boolean nextQuestion() {
        if (currentQuestionIndex + 1 < quiz.getQuestions().size()) {
            currentQuestionIndex++;
            return true;
        }
        return false;
    }

    // Atualizar pontuação
    public void addScore(String teamName, int points) {
        Team t = teams.get(teamName);
        if (t != null) {
            // Para simplificar, atribuímos pontos a todos os jogadores
            for (Player p : t.getMembers()) {
                p.addScore(points);
            }
        }
    }

    public Map<String, Integer> getTeamScores() {
        Map<String, Integer> scores = new HashMap<>();
        for (Team t : teams.values()) {
            scores.put(t.getName(), t.getTotalScore());
        }
        return scores;
    }
}
