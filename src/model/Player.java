package model;

public class Player {
    private String username;
    private String teamName;
    private int score;

    public Player(String username, String teamName) {
        this.username = username;
        this.teamName = teamName;
        this.score = 0;
    }

    public String getUsername() { return username; }
    public String getTeamName() { return teamName; }
    public int getScore() { return score; }

    public void addScore(int points) {
        this.score += points;
    }
}
