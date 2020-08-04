package Main;

import java.util.Random;

public class Player {
    public int ID;
    private String username;
    private String password;
    private int score;

    public Player(String username, String password, int score) {
        this.username = username;
        this.password = password;
        this.score = score;
        ID = new Random().nextInt();
    }

    public Player(int ID, String username, String password, int score) {
        this.ID = ID;
        this.username = username;
        this.password = password;
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
