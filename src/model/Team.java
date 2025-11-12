package model;

import java.util.ArrayList;
import java.util.List;

public class Team {

    private String name;
    private List<Player> members = new ArrayList<>();
    private int totalScore = 0;

    public Team(String name) {
        this.name = name;
    }

    public void addPlayer(Player p) {
        members.add(p);
    }

    public List<Player> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    public void addPoints(int points) {
        totalScore += points;
    }

    public int getTotalScore() {
        return totalScore;
    }
}
