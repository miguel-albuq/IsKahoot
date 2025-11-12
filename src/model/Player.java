package model;

public class Player {
    private String name;
    private String teamName;
    private int score = 0;

    public Player(String name, String teamName) {
        this.name = name;
        this.teamName = teamName;
    }

    public String getName() {
        return name;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }
}
