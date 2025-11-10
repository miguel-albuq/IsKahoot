package utils;

import model.*;

public class TestJSON {
    public static void main(String[] args) {
        QuizCollection qc = JSONLoader.load("resources/questions.json");

        if (qc != null) {
            for (Quiz quiz : qc.getQuizzes()) {
                System.out.println("Quiz: " + quiz.getName());
                for (Question q : quiz.getQuestions()) {
                    System.out.println("- Pergunta: " + q.getQuestion());
                    System.out.println("  Pontos: " + q.getPoints());
                    System.out.println("  Opções: " + q.getOptions());
                }
            }
        } else {
            System.out.println("Erro ao ler ficheiro JSON!");
        }


    }
}
