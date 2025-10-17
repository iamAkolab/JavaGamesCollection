package com.games;

import java.awt.Color;
import java.awt.Graphics;

public class Ball {
    public static final int SIZE = 16;

    private int x, y;
    private int xVel, yVel;
    private int speed = 5;

    public Ball() {

        reset();

    }

    private void reset() {

        x = Main.WIDTH/2 - SIZE/2;
        y = Main.HEIGHT/2 - SIZE/2;

        xVel = Main.sign(Math.random()*2 -1);
        yVel = Main.sign(Math.random()*2 -1);

    }

    public void changeXDir() {
        xVel *= -1;
    }

    public void changeYDir() {
        yVel *= -1;
    }

    public void draw(Graphics g) {

        g.setColor(Color.white);
        g.fillOval(x, y, SIZE, SIZE);

    }

    public void update(Paddle paddle1, Paddle paddle2) {
        // ball movement
        x += xVel * speed;
        y += yVel * speed;

        // for collisions with upper and lower walls
        if(y >= Main.HEIGHT - SIZE || y<= 0) {
            changeYDir();
        }

        // for paddle miss
        if(x >= Main.WIDTH - SIZE) {
            paddle1.addPoint();
            reset();
        }
        if(x <= 0) {
            paddle2.addPoint();
            reset();
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
