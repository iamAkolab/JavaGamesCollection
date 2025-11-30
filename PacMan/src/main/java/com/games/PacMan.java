package com.games;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel {

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

}
