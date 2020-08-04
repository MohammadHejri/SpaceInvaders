package Game;

import javafx.scene.image.Image;

public class Invader extends Sprite {
    private int horizontalMove;
    private int sign = +1;

    public Invader(Game game, int posX, int posY, int size, Image image) {
        super(game, posX, posY, size, image);
    }

    @Override
    public Bullet shootBullet() {
        return new Bullet(game,"invader",posX + size / 2 - Bullet.size / 2, posY + size + Bullet.size);
    }

    @Override
    public void update() {
        super.update();
        if (horizontalMove > 200) {
            sign = -1;
        } else if (horizontalMove < -200) {
            sign = +1;
        }
        posX += sign * (game.getScore() / 20 + 2);
        horizontalMove += sign * (game.getScore() / 20 + 2);
        if (posY > game.getHEIGHT())
            destroyed = true;
    }

    public void moveDown() {
        posY += size;
    }
}
