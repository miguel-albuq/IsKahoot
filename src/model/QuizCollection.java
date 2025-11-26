package model;

import java.util.*;

public class QuizCollection {
    private List<Quiz> quizzes;

    public QuizCollection() {
        quizzes = new ArrayList<>();
    }

    // Adicionar um quiz ao QuizCollection
    public void addQuiz(Quiz quiz) {
        quizzes.add(quiz);
    }

    // Obter a lista de quizzes
    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}
