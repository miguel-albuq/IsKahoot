package model;

import java.util.List;

public class Question {
    private String question;
    private int points;
    private int correct;
    private List<String> options;

    // Getters (necessários para o Gson funcionar)
    public String getQuestion() { return question; }
    public int getPoints() { return points; }
    public int getCorrect() { return correct; }
    public List<String> getOptions() { return options; }
}
