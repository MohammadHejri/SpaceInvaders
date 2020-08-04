package Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Database {
    private static Database instance;
    private final String playersPath = "src/resources/players";
    private ArrayList<Player> allPlayers = new ArrayList<>();
    public Player signedInPlayer;

    public static Database getInstance() {
        if (instance == null)
            instance = new Database();
        return instance;
    }

    public void loadPlayers() {
        try {
            new File(playersPath).mkdirs();
            for (File playerFile : new File(playersPath).listFiles()) {
                Scanner scanner = new Scanner(playerFile);
                int ID = Integer.parseInt(scanner.nextLine());
                String username = scanner.nextLine();
                String password = scanner.nextLine();
                int score = Integer.parseInt(scanner.nextLine());
                allPlayers.add(new Player(ID, username, password, score));
            }
        } catch (Exception e) {
            System.err.println("Loading players failed...");
            System.exit(1);
        }
    }

    public void savePlayer(Player player) throws IOException {
        try {
            if (getPlayerByUsername(player.getUsername()) == null)
                allPlayers.add(player);
            FileWriter writer = new FileWriter(playersPath + "/" + player.ID + ".txt");
            writer.write(player.ID + "\n" + player.getUsername() + "\n" + player.getPassword() + "\n" + player.getScore() + "\n");
            writer.close();
        } catch (Exception e) {
            System.err.println("Saving player failed...");
            System.exit(1);
        }
    }

    public Player getPlayerByUsername(String username) {
        for (Player player : allPlayers)
            if (player.getUsername().equals(username))
                return player;
        return null;
    }

    public ArrayList<Player> getAllPlayers() {
        return allPlayers;
    }

    public ArrayList<Player> getSortedPlayers() {
        ArrayList<Player> sorted = new ArrayList<>();
        for (Player p : allPlayers)
            sorted.add(new Player(p.getUsername(), p.getPassword(), p.getScore()));
        Collections.sort(sorted, new SortByScore());
        int rank = 1;
        sorted.get(0).setPassword("1");
        for (int i = 1; i < sorted.size(); i++) {
            Player curP = sorted.get(i);
            Player prevP = sorted.get(i-1);
            if (curP.getScore() == prevP.getScore())
                curP.setPassword(String.valueOf(rank));
            else
                curP.setPassword(String.valueOf(rank = i+1));
        }
        return sorted;
    }

    class SortByScore implements Comparator<Player> {
        public int compare(Player p1, Player p2) {
            return p2.getScore() - p1.getScore();
        }
    }
}
