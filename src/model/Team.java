package model;

import java.util.ArrayList;
import java.util.List;

public class Team {
    private String name;
    private List<Player> members = new ArrayList<>();

    public Team(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void addPlayer(Player p) {
        members.add(p);
    }

    public List<Player> getMembers() { return members; }

    public int getTotalScore() {
        return members.stream().mapToInt(Player::getScore).sum();
    }
}
