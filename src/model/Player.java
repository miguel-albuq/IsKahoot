package model;

public class Player {
    private String username;
    private String teamName;
    private int score = 0;

    public Player(String username, String teamName) {
        this.username = username;
        this.teamName = teamName;
    }

    public String getName() {
        return username;
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
