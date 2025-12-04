package com.games;

import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    enum BlockType {
        WALL,
        FOOD,
        POWER_PELLET,
        GHOST,
        PACMAN
    }

    BlockType type = BlockType.WALL;



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

        // ✅ NEW FIELDS FOR POWER-UPS & GHOST STATE
        BlockType type;
        boolean vulnerable = false; // for ghosts
        long vulnerableUntil = 0;   // timestamp (in ms) when vulnerability ends
        boolean eaten = false;

        Block(Image image, int x, int y, int width, int height, BlockType type) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
            this.type = type;
        }

        void updateDirection(char direction) {
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            int speed = tileSize / 4; // 8 px/frame
            switch (direction) {
                case 'U':
                    velocityX = 0;
                    velocityY = -speed;
                    break;
                case 'D':
                    velocityX = 0;
                    velocityY = speed;
                    break;
                case 'L':
                    velocityX = -speed;
                    velocityY = 0;
                    break;
                case 'R':
                    velocityX = speed;
                    velocityY = 0;
                    break;
                default:
                    velocityX = 0;
                    velocityY = 0;
            }
        }
//        void updateVelocity() {
//            if (this.direction == 'U') {
//                this.velocityX = 0;
//                this.velocityY = -tileSize/4;
//            }
//            else if (this.direction == 'D') {
//                this.velocityX = 0;
//                this.velocityY = tileSize/4;
//            }
//            else if (this.direction == 'L') {
//                this.velocityX = -tileSize/4;
//                this.velocityY = 0;
//            }
//            else if (this.direction == 'R') {
//                this.velocityX = tileSize/4;
//                this.velocityY = 0;
//            }
//        }

        void reset() {
            this.x = this.startX;
            this.y = this.startY;
            eaten = false;
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
    private Image powerPelletImage;
    private Image powerPelletImage2; // for animation
    private Image vulnerableGhostImage; // optional: use blueGhostImage or create a new one

    //PacMan Image
    private Image pacManUpImage;
    private Image pacManDownImage;
    private Image pacManLeftImage;
    private Image pacManRightImage;


    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "XC       X       CX",
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
            "XC               CX",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;
    HashSet<Block> powerPellets;
    HashSet<Block> ghosts;
    Block pacman;

    Timer gameLoop;
    char[] directions = {'U', 'D', 'L', 'R'}; //up down left right
    Random random = new Random();
    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    PacMan() {
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);

        //load images
        wallImage = new ImageIcon(getClass().getResource("/images/wall.png")).getImage();

        blueGhostImage = new ImageIcon(getClass().getResource("/images/blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("/images/orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("/images/pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("/images/redGhost.png")).getImage();

        powerPelletImage = new ImageIcon(getClass().getResource("/images/cherry.png")).getImage();
        powerPelletImage2 = new ImageIcon(getClass().getResource("/images/cherry2.png")).getImage();
        vulnerableGhostImage = blueGhostImage;

        pacManUpImage = new ImageIcon(getClass().getResource("/images/pacmanUp.png")).getImage();
        pacManDownImage = new ImageIcon(getClass().getResource("/images/pacmanDown.png")).getImage();
        pacManLeftImage = new ImageIcon(getClass().getResource("/images/pacmanLeft.png")).getImage();
        pacManRightImage = new ImageIcon(getClass().getResource("/images/pacmanRight.png")).getImage();

        loadMap();
        for (Block ghost : ghosts) {
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
        //how long it takes to start timer, milliseconds gone between frames
        gameLoop = new Timer(100, this); //20fps (1000/50)
        gameLoop.start();
    }

    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        powerPellets = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                String row = tileMap[r];
                char tileMapChar = row.charAt(c);

                int x = c * tileSize;
                int y = r * tileSize;

                switch (tileMapChar) {
                    case 'X':
                        walls.add(new Block(wallImage, x, y, tileSize, tileSize, BlockType.WALL));
                        break;

                    case 'b':
                        ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize, BlockType.GHOST));
                        break;
                    case 'o':
                        ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize, BlockType.GHOST));
                        break;
                    case 'p':
                        ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize, BlockType.GHOST));
                        break;
                    case 'r':
                        ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize, BlockType.GHOST));
                        break;

                    case 'P':
                        pacman = new Block(pacManRightImage, x, y, tileSize, tileSize, BlockType.PACMAN);
                        break;

                    case ' ':
                        foods.add(new Block(null, x + 12, y + 12, 8, 8, BlockType.FOOD));
                        break;

                    case 'C': // Power pellet (Cherry)
                        powerPellets.add(new Block(powerPelletImage, x + 8, y + 8, 16, 16, BlockType.POWER_PELLET));
                        break;

                    // Ignore 'O' (empty/open space)
                }

//                if (tileMapChar == 'X') { //block wall
//                    Block wall = new Block(wallImage, x, y, tileSize, tileSize, BlockType.WALL);
//                    walls.add(wall);
//                }
//                else if (tileMapChar == 'b') { //blue ghost
//                    Block ghost = new Block(blueGhostImage, x, y, tileSize, tileSize, BlockType.GHOST);
//                    ghosts.add(ghost);
//                }
//                else if (tileMapChar == 'o') { //orange ghost
//                    Block ghost = new Block(orangeGhostImage, x, y, tileSize, tileSize, BlockType.GHOST);
//                    ghosts.add(ghost);
//                }
//                else if (tileMapChar == 'p') { //pink ghost
//                    Block ghost = new Block(pinkGhostImage, x, y, tileSize, tileSize, BlockType.GHOST);
//                    ghosts.add(ghost);
//                }
//                else if (tileMapChar == 'r') { //red ghost
//                    Block ghost = new Block(redGhostImage, x, y, tileSize, tileSize, BlockType.GHOST);
//                    ghosts.add(ghost);
//                }
//                else if (tileMapChar == 'P') { //pacman
//                    pacman = new Block(pacManRightImage, x, y, tileSize, tileSize, BlockType.PACMAN);
//                }
//                else if (tileMapChar == ' ') { //food
//                    Block food = new Block(null, x + 14, y + 14, 4, 4, BlockType.FOOD);
//                    foods.add(food);
//                }
//                else if (tileMapChar == 'C') { // Power pellet (Cherry)
//                    Block pellet = new Block(powerPelletImage, x + 8, y + 8, 16, 16, BlockType.POWER_PELLET); // larger
//                    foods.add(pellet); // reuse foods set, or create powerPellets set (see note below)
//                }
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        for (Block ghost : ghosts) {
            Image img = ghost.image;
            if (ghost.vulnerable) {
                // Optional: flicker near end
                long now = System.currentTimeMillis();
                if (ghost.vulnerableUntil - now < 2000 && (now / 250) % 2 == 0) {
                    img = null; // blink
                } else {
                    img = vulnerableGhostImage; // e.g., blue
                }
            }
            if (img != null) {
                g.drawImage(img, ghost.x, ghost.y, ghost.width, ghost.height, null);
            }
        }

        g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);

        for (Block ghost : ghosts) {
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        // Draw regular food
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillOval(food.x, food.y, food.width, food.height);
        }

        // Draw power pellets (animated)
        long time = System.currentTimeMillis();
        for (Block pellet : powerPellets) {
            // Alternate every 500ms
            Image img = (time / 500) % 2 == 0 ? powerPelletImage : powerPelletImage2;
            g.drawImage(img, pellet.x, pellet.y, pellet.width, pellet.height, null);
        }

        //score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
        else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    public void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;

        //check wall collisions
        for (Block wall : walls) {
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        //check ghost collisions
        for (Block ghost : ghosts) {
            if (collision(ghost, pacman)) {
                lives -= 1;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }

            if (ghost.y == tileSize*9 && ghost.direction != 'U' && ghost.direction != 'D') {
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //check food collision
        Block foodEaten = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodEaten = food;
                score += 10;
                break;
            }
        }
        foods.remove(foodEaten);

        // Check power pellet collision
        Block pelletEaten = null;
        for (Block pellet : powerPellets) {
            if (collision(pacman, pellet)) {
                pelletEaten = pellet;
                score += 50;

                // Activate vulnerability for 10 seconds (10,000 ms)
                long now = System.currentTimeMillis();
                for (Block ghost : ghosts) {
                    ghost.vulnerable = true;
                    ghost.vulnerableUntil = now + 10_000;
                    ghost.eaten = false;
                }
                break;
            }
        }
        powerPellets.remove(pelletEaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    public boolean collision(Block a, Block b) {
        return  a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    public void resetPositions() {
        if (pacman != null) pacman.reset();

        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.vulnerable = false;
            ghost.eaten = false;
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        // System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP) {
            pacman.updateDirection('U');
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            pacman.updateDirection('D');
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }

        if (pacman.direction == 'U') {
            pacman.image = pacManUpImage;
        }
        else if (pacman.direction == 'D') {
            pacman.image = pacManDownImage;
        }
        else if (pacman.direction == 'L') {
            pacman.image = pacManLeftImage;
        }
        else if (pacman.direction == 'R') {
            pacman.image = pacManRightImage;
        }
    }

}
