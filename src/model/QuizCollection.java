package model;

import java.util.List;

/**
 * Usado apenas para ler o JSON com o array "quizzes".
 */
public class QuizCollection {
    private List<Quiz> quizzes;

    public List<Quiz> getQuizzes() {
        return quizzes;
    }
}
