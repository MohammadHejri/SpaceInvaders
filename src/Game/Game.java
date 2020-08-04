package Game;

import java.io.File;
import java.io.IOException;
import java.util.*;

import Main.*;
import Main.Database;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Game extends Application {
    private final int WIDTH = 800;
    private final int HEIGHT = 600;
    private final int SPIRIT_SIZE = 60;
    private final int MAX_TIME;
    private final int MIN_TIME;
    private int score;
    private Sprite player;
    private ArrayList<Invader> allInvader;
    private ArrayList<Bullet> allBullets;
    private ArrayList<Wall> allWalls;
    private GraphicsContext graphicsContext;
    private boolean gameOver = false;
    private boolean bulletAllowed = true;
    private Timeline moveDownTimeline;
    private Timeline invaderShootTimeline;
    private final Media bulletSound = new Media(new File("src/resources/sounds/bullet.wav").toURI().toString());
    private final Media wallBreakSound = new Media(new File("src/resources/sounds/wallBreak.wav").toURI().toString());
    private final Media invaderBulletSound = new Media(new File("src/resources/sounds/invaderBullet.wav").toURI().toString());
    private final Media playerKilledSound = new Media(new File("src/resources/sounds/playerKilled.wav").toURI().toString());
    private final Media invaderKilledSound = new Media(new File("src/resources/sounds/invaderKilled.wav").toURI().toString());
    private final Image playerImage = new Image("resources/images/player.png");
    private final Image wallImage = new Image("resources/images/wall.png");
    private final Image explosionImage = new Image("resources/images/explosion.png");
    private final Image backgroundImage = new Image("resources/images/bg.png");
    private final Image[] invadersImage = {
            new Image("resources/images/1.png"),
            new Image("resources/images/2.png"),
            new Image("resources/images/3.png"),
            new Image("resources/images/4.png"),
            new Image("resources/images/5.png"),
    };

    public Game(int MAX_TIME, int MIN_TIME) {
        this.MAX_TIME = MAX_TIME;
        this.MIN_TIME = MIN_TIME;
    }

    public void start(Stage stage) throws Exception {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        graphicsContext = canvas.getGraphicsContext2D();
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> runGraphics(graphicsContext)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        initGame();
        Scene scene = new Scene(new StackPane(canvas), WIDTH, HEIGHT);
        scene.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.LEFT))
                player.moveLeft();
            if (e.getCode().equals(KeyCode.RIGHT))
                player.moveRight();
            if (e.getCode().equals(KeyCode.SPACE) && !gameOver && bulletAllowed) {
                new AudioClip(bulletSound.getSource()).play();
                allBullets.add(player.shootBullet());
                bulletAllowed = false;
            }
            if (e.getCode().equals(KeyCode.ENTER) && gameOver) {
                try {
                    new Main().start(stage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (e.getCode().equals(KeyCode.ESCAPE) && !gameOver && !player.exploding) {
                new AudioClip(playerKilledSound.getSource()).play();
                player.explode();
            }
        });
        scene.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.SPACE) && !gameOver && !bulletAllowed)
                bulletAllowed = true;
            if (e.getCode().equals(KeyCode.LEFT))
                player.moveLeft();
            if (e.getCode().equals(KeyCode.RIGHT))
                player.moveRight();
        });
        stage.setScene(scene);
        stage.show();
    }

    private void initGame() {
        allInvader = new ArrayList<>();
        allBullets = new ArrayList<>();
        allWalls = new ArrayList<>();
        player = new Sprite(this, WIDTH / 2 - SPIRIT_SIZE / 2, HEIGHT - SPIRIT_SIZE - 10, SPIRIT_SIZE, playerImage);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 6; j++) {
                allInvader.add(new Invader(this, 225 + 60 * j, 50 + 50 * i, 50, invadersImage[i]));
            }
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < WIDTH / 8; j++) {
                if (j % 22 < 12)
                    continue;
                allWalls.add(new Wall(this, 8*j, HEIGHT - 2 *SPIRIT_SIZE + 8*i, 8, wallImage));
            }
        }
        score = 0;
        moveDownTimeline = new Timeline(new KeyFrame(Duration.seconds(MAX_TIME), e -> moveInvadersDown()));
        moveDownTimeline.play();
        invaderShootTimeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> invaderShoot()));
        invaderShootTimeline.setCycleCount(Timeline.INDEFINITE);
        invaderShootTimeline.play();
    }

    private void moveInvadersDown() {
        for (Invader invader : allInvader) {
            invader.moveDown();
        }
        int colorIndex = new Random().nextInt(5);
        for (int j = 0; j < 6; j++) {
            allInvader.add(new Invader(this, 225 + 60 * j, 50, 50, invadersImage[colorIndex]));
        }
        double time = score > 100 ? MIN_TIME : MAX_TIME - (MAX_TIME - MIN_TIME) * score / 100;
        if (!gameOver)
            new Timeline(new KeyFrame(Duration.seconds(time), e -> moveInvadersDown())).play();
    }

    private void invaderShoot() {
        Random random = new Random();
        if (allInvader.size() >= 1) {
            if (Math.random() < 0.5) {
                allBullets.add(allInvader.get(random.nextInt(allInvader.size())).shootBullet());
                new AudioClip(invaderBulletSound.getSource()).play();
            }
            if (Math.random() < 0.5) {
                allBullets.add(allInvader.get(random.nextInt(allInvader.size())).shootBullet());
                new AudioClip(invaderBulletSound.getSource()).play();
            }
            if (Math.random() < 0.5) {
                allBullets.add(allInvader.get(random.nextInt(allInvader.size())).shootBullet());
                new AudioClip(invaderBulletSound.getSource()).play();
            }
        }
    }
    private void runGraphics(GraphicsContext gc) {
        gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font(20));
        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 60,  40);
        if(gameOver) {
            Player player = Database.getInstance().signedInPlayer;
            if (player.getScore() < score)
                player.setScore(score);
            try {
                Database.getInstance().savePlayer(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Invader invader : allInvader) {
                if (!invader.exploding)
                    invader.explode();
            }
            for (Wall wall : allWalls) {
                if (!wall.exploding)
                    wall.explode();
            }
            invaderShootTimeline.stop();
            gc.setFont(Font.font(40));
            gc.setFill(Color.ORANGE);
            gc.fillText("Game Over", WIDTH / 2, HEIGHT / 3 + 40);
            gc.setFont(Font.font(25));
            gc.setFill(Color.ORANGE);
            gc.fillText("press Enter to return to main menu", WIDTH / 2, HEIGHT / 3 + 100);
        }
        player.update();
        player.draw();
        for (Invader invader : allInvader) {
            invader.update();
            invader.draw();
            if (invader.crash(player) && !player.exploding) {
                new AudioClip(playerKilledSound.getSource()).play();
                player.explode();
            }
            for (Wall wall : allWalls) {
                if (invader.crash(wall) && !wall.exploding) {
                    new AudioClip(wallBreakSound.getSource()).play();
                    wall.explode();
                }
            }
        }
        ArrayList<Bullet> newBullets = new ArrayList<>();
        for (Bullet bullet : allBullets) {
            if(!bullet.toBeRemoved)  {
                newBullets.add(bullet);
                bullet.update();
                bullet.draw();
                if (bullet.getOwner().equals("player")) {
                    for (Invader invader : allInvader) {
                        if (bullet.crash(invader) && !invader.exploding) {
                            new AudioClip(invaderKilledSound.getSource()).play();
                            invader.explode();
                            bullet.toBeRemoved = true;
                            score++;
                        }
                    }
                }
                if (bullet.getOwner().equals("invader")) {
                    if (bullet.crash(player) && !player.exploding) {
                        new AudioClip(playerKilledSound.getSource()).play();
                        player.explode();
                        bullet.toBeRemoved = true;
                    }
                }
                for (Wall wall : allWalls) {
                    if (bullet.crash(wall) && !wall.exploding) {
                        new AudioClip(wallBreakSound.getSource()).play();
                        wall.explode();
                        bullet.toBeRemoved = true;
                    }
                }
            }
        }
        allBullets = newBullets;
        ArrayList<Wall> newWalls = new ArrayList<>();
        for (Wall wall : allWalls) {
            if (!wall.destroyed) {
                newWalls.add(wall);
                wall.update();
                wall.draw();
            }
        }
        allWalls = newWalls;
        ArrayList<Invader> newInvaders = new ArrayList<>();
        for (Invader invader : allInvader) {
            if (!invader.destroyed)
                newInvaders.add(invader);
        }
        allInvader = newInvaders;
        gameOver = player.destroyed;
    }

    public Image getExplosionImage() {
        return explosionImage;
    }

    public int getScore() {
        return score;
    }

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }
}
