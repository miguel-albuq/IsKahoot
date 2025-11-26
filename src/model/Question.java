package model;

import java.util.List;

public class Question {
    private String text;
    private int points;
    private int correctAnswer;
    private List<String> options;

    public Question(String text, int points, int correctAnswer, List<String> options) {
        this.text = text;
        this.points = points;
        this.correctAnswer = correctAnswer;
        this.options = options;
    }

    public String getQuestion() {
        return text;
    }

    public int getPoints() {
        return points;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getOptions() {
        return options;
    }
}
