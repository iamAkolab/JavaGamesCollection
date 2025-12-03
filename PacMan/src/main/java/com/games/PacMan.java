package com.games;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel {


    class Block {
        int x;
        int y;
        int width;
        int height;
        Image image;

        int startX;
        int startY;
        char direction = 'U'; // U D L R
        int velocityX = 0;
        int velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }
    }


        private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;

    //GhostImages
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    //PacMan Image
    private Image pacManUpImage;
    private Image pacManDownImage;
    private Image pacManLeftImage;
    private Image pacManRightImage;

    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> ghosts;
    Block pacman;



    PacMan() {
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);

        //load images
        wallImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/wall.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/redGhost.png")).getImage();

        pacManUpImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/pacmanUp.png")).getImage();
        pacManDownImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/pacmanDown.png")).getImage();
        pacManLeftImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/pacmanLeft.png")).getImage();
        pacManRightImage = new ImageIcon(getClass().getResource("./src/main/java/resources/images/pacmanRight.png")).getImage();
    }


    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {
    }

}
