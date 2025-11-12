package utils;

import model.*;

public class TestGameState {
    public static void main(String[] args) {
        // Carregar quiz
        QuizCollection qc = JSONLoader.load("resources/questions.json");
        Quiz quiz = qc.getQuizzes().get(0);
    
        // Criar estado do jogo
        GameState state = new GameState(quiz);

        Team teamA = new Team("Team A");
        teamA.addPlayer(new Player("Alice", "Team A"));
        teamA.addPlayer(new Player("Bob", "Team A"));
        state.addTeam(teamA);

        // Mostrar perguntas e pontuação
        Question q = state.getCurrentQuestion();
        System.out.println("Pergunta: " + q.getQuestion());
        System.out.println("Opções: " + q.getOptions());

        state.addScore("Team A", 10);
        System.out.println("Pontuação: " + state.getTeamScores());

        state.nextQuestion();
        System.out.println("Próxima Pergunta: " + state.getCurrentQuestion().getQuestion());
    }
}
