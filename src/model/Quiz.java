package model;

import java.util.*;

public class Quiz {
    private String name;
    private List<Question> questions;

    public Quiz(String name) {
        this.name = name;
        this.questions = new ArrayList<>();
    }

    // Adicionar uma pergunta ao quiz
    public void addQuestion(Question question) {
        questions.add(question);
    }

    // Obter todas as perguntas do quiz
    public List<Question> getQuestions() {
        return questions;
    }

    // Obter o nome do quiz
    public String getName() {
        return name;
    }
}
