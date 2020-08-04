package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.geom.Point2D;

public class Sprite {
    protected Game game;
    protected int posX;
    protected int posY;
    protected int size;
    protected int explosionStep;
    protected boolean exploding;
    protected boolean destroyed;
    protected Image image;

    public Sprite(Game game, int posX, int posY, int size, Image image) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
        this.size = size;
        this.image = image;
    }

    public Bullet shootBullet() {
        return new Bullet(game, "player", posX + size / 2 - Bullet.size / 2, posY - Bullet.size);
    }

    public void update() {
        if (exploding) explosionStep++;
        destroyed = explosionStep > 15;
    }

    public void draw() {
        GraphicsContext gc = game.getGraphicsContext();
        if(exploding) {
            Image image = game.getExplosionImage();
            int X = explosionStep % 4 * 128;
            int Y = explosionStep / 4 * 128;
            gc.drawImage(image, X, Y, 128, 128, posX, posY, size, size);
        } else {
            gc.drawImage(image, posX, posY, size, size);
        }
    }

    public boolean crash(Sprite other) {
        int x1 = this.posX + this.size / 2;
        int y1 = this.posY + this.size / 2;
        int x2 = other.posX + other.size / 2;
        int y2 = other.posY + other.size / 2;
        double distance = Point2D.distance(x1, y1, x2, y2);
        return distance < other.size / 2 + this.size / 2 ;
    }

    public void explode() {
        exploding = true;
        explosionStep = -1;
    }

    public void moveLeft() {
        if (posX > 0) {
            posX -= game.getScore() / 10 + 10;
        }
    }

    public void moveRight() {
        if (posX < game.getWIDTH() - size) {
            posX += game.getScore() / 10 + 10;
        }
    }

}
