package Game;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.awt.geom.Point2D;

public class Bullet {
    private Game game;
    private String owner;
    private int posX;
    private int posY;
    public static int size = 6;
    public boolean toBeRemoved;

    public Bullet(Game game, String owner, int posX, int posY) {
        this.game = game;
        this.owner = owner;
        this.posX = posX;
        this.posY = posY;
    }

    public void update() {
        if (owner.equals("invader"))
            posY += game.getScore() / 5 + 10;
        else
            posY -= game.getScore() / 10 + 10;
        if (posY < 0 || posY > game.getHEIGHT())
            toBeRemoved = true;
    }

    public void draw() {
        GraphicsContext gc = game.getGraphicsContext();
        if (owner.equals("invader")) {
            gc.setFill(Color.BLANCHEDALMOND);
            gc.fillRect(posX, posY, size, size * 3);
        }
        else {
            gc.setFill(Color.CORAL);
            gc.fillOval(posX, posY, size, size);
        }
    }

    public boolean crash(Sprite sprite) {
        int x1 = this.posX + size / 2;
        int y1 = this.posY + size / 2;
        int x2 = sprite.posX + sprite.size / 2;
        int y2 = sprite.posY + sprite.size / 2;
        double distance = Point2D.distance(x1, y1, x2, y2);
        return distance < sprite.size / 2 + size / 2 ;
    }

    public String getOwner() {
        return owner;
    }
}
