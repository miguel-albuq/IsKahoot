package model;

import java.util.List;

public class Question {
    private String question;
    private int points;
    private int correct; // index da resposta correta
    private List<String> options;

    public String getQuestion() {
        return question;
    }

    public int getPoints() {
        return points;
    }

    public int getCorrect() {
        return correct;
    }

    public List<String> getOptions() {
        return options;
    }
}
