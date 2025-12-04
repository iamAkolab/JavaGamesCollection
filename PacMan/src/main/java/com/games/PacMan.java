package com.games;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener {

    // ===== ENUMS =====
    enum BlockType {
        WALL,
        FOOD,
        POWER_PELLET,
        GHOST,
        PACMAN
    }

    // ===== INNER CLASS: Block =====
    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'R'; // default: right
        int velocityX = 0, velocityY = 0;

        // Power-up & state
        BlockType type;
        boolean vulnerable = false;
        long vulnerableUntil = 0;
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

        void updateDirection(char newDirection) {
            this.direction = newDirection;
            updateVelocity();
        }

        void updateVelocity() {
            int speed = tileSize / 4; // 8 px/frame @ 20 FPS
            switch (direction) {
                case 'U': velocityX = 0; velocityY = -speed; break;
                case 'D': velocityX = 0; velocityY =  speed; break;
                case 'L': velocityX = -speed; velocityY = 0; break;
                case 'R': velocityX =  speed; velocityY = 0; break;
                default:  velocityX = 0; velocityY = 0;
            }
        }

        void reset() {
            x = startX;
            y = startY;
            eaten = false;
            vulnerable = false;
        }
    }

    // ===== CONSTANTS & FIELDS =====
    private static final int rowCount = 21;
    private static final int columnCount = 19;
    private static final int tileSize = 32;
    private static final int boardWidth = columnCount * tileSize;
    private static final int boardHeight = rowCount * tileSize;

    // Images
    private Image wallImage;
    private Image blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacManUpImage, pacManDownImage, pacManLeftImage, pacManRightImage;
    private Image powerPelletImage, powerPelletImage2;
    private Image vulnerableGhostImage; // use blueGhostImage as fallback

    // Game state
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "XC       X       CX",
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

    private HashSet<Block> walls;
    private HashSet<Block> foods;
    private HashSet<Block> powerPellets;
    private HashSet<Block> ghosts;
    private Block pacman;

    private Timer gameLoop;
    private final char[] directions = {'U', 'D', 'L', 'R'};
    private final Random random = new Random();
    private int score = 0;
    private int lives = 3;
    private boolean gameOver = false;

    // ===== CONSTRUCTOR =====
    public PacMan() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        // Load images safely
        wallImage = loadImage("/images/wall.png", "wall");
        blueGhostImage = loadImage("/images/blueGhost.png", "blue ghost");
        orangeGhostImage = loadImage("/images/orangeGhost.png", "orange ghost");
        pinkGhostImage = loadImage("/images/pinkGhost.png", "pink ghost");
        redGhostImage = loadImage("/images/redGhost.png", "red ghost");

        pacManUpImage = loadImage("/images/pacmanUp.png", "pacman up");
        pacManDownImage = loadImage("/images/pacmanDown.png", "pacman down");
        pacManLeftImage = loadImage("/images/pacmanLeft.png", "pacman left");
        pacManRightImage = loadImage("/images/pacmanRight.png", "pacman right");

        powerPelletImage = loadImage("/images/cherry.png", "cherry");
        powerPelletImage2 = loadImage("/images/Cherry2.png", "cherry alt");
        vulnerableGhostImage = blueGhostImage; // reuse blue

        // Initialize game
        loadMap();
        resetPositions();

        // Start game loop (20 FPS)
        gameLoop = new Timer(50, this);
        gameLoop.start();

        // Ensure focus
        requestFocusInWindow();
    }

    // ===== IMAGE LOADER WITH FALLBACK =====
    private Image loadImage(String path, String name) {
        URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("❌ Missing image: " + path + " (" + name + ")");
            BufferedImage placeholder = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = placeholder.createGraphics();
            g2d.setColor(Color.MAGENTA);
            g2d.fillRect(0, 0, tileSize, tileSize);
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Sans", Font.BOLD, 10));
            String label = name.length() > 6 ? name.substring(0, 6) : name;
            g2d.drawString(label, 2, tileSize - 5);
            g2d.dispose();
            return placeholder;
        }
        return new ImageIcon(url).getImage();
    }

    // ===== MAP LOADING =====
    public void loadMap() {
        walls = new HashSet<>();
        foods = new HashSet<>();
        powerPellets = new HashSet<>();
        ghosts = new HashSet<>();

        for (int r = 0; r < rowCount; r++) {
            String row = tileMap[r];
            for (int c = 0; c < columnCount; c++) {
                char ch = row.charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                switch (ch) {
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
                    case 'C':
                        powerPellets.add(new Block(powerPelletImage, x + 8, y + 8, 16, 16, BlockType.POWER_PELLET));
                        break;
                    // 'O' and others: empty
                }
            }
        }
    }

    // ===== RESET POSITIONS =====
    public void resetPositions() {
        if (pacman != null) {
            pacman.reset();
            pacman.updateVelocity(); // critical!
        }
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
        score = 0;
        lives = 3;
        gameOver = false;
    }

    // ===== GAME LOOP: actionPerformed =====
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
        }
        repaint();
    }

    // ===== MOVEMENT LOGIC =====
    public void move() {
        long now = System.currentTimeMillis();

        // Decay ghost vulnerability
        for (Block ghost : ghosts) {
            if (ghost.vulnerable && now >= ghost.vulnerableUntil) {
                ghost.vulnerable = false;
                ghost.eaten = false;
            }
        }

        // === Move Pac-Man ===
        int newX = pacman.x + pacman.velocityX;
        int newY = pacman.y + pacman.velocityY;

        // ✅ Wrap-around tunnel (left ↔ right)
        if (newX + pacman.width < 0) {
            newX = boardWidth;
        } else if (newX > boardWidth) {
            newX = -pacman.width;
        }

        // Wall collision check
        boolean canMove = true;
        for (Block wall : walls) {
            Block temp = new Block(null, newX, newY, pacman.width, pacman.height, BlockType.WALL);
            if (collision(temp, wall)) {
                canMove = false;
                break;
            }
        }

        if (canMove) {
            pacman.x = newX;
            pacman.y = newY;
        }

        // === Move ghosts ===
        for (Block ghost : ghosts) {
            if (ghost.eaten) {
                // Optional: return to center faster
                continue;
            }

            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;

            // Wall/edge collision
            boolean collided = false;
            for (Block wall : walls) {
                if (collision(ghost, wall) || ghost.x < 0 || ghost.x + ghost.width > boardWidth) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                    collided = true;
                    break;
                }
            }

            // Random turn occasionally (smarter AI later)
            if (!collided && random.nextInt(100) < 3) {
                ghost.updateDirection(directions[random.nextInt(4)]);
            }
        }

        // === Pac-Man vs Ghost collision ===
        for (Block ghost : ghosts) {
            if (collision(pacman, ghost)) {
                if (ghost.vulnerable && !ghost.eaten) {
                    // Eat ghost!
                    ghost.eaten = true;
                    score += 200;
                    ghost.reset();
                } else if (!ghost.eaten) {
                    // Lose life
                    lives--;
                    if (lives <= 0) {
                        gameOver = true;
                    } else {
                        resetPositions();
                    }
                }
            }
        }

        // === Food collision ===
        Block foodHit = null;
        for (Block food : foods) {
            if (collision(pacman, food)) {
                foodHit = food;
                score += 10;
                break;
            }
        }
        foods.remove(foodHit);

        // === Power pellet collision ===
        Block pelletHit = null;
        for (Block pellet : powerPellets) {
            if (collision(pacman, pellet)) {
                pelletHit = pellet;
                score += 50;
                // Activate vulnerability for 10 seconds
                for (Block g : ghosts) {
                    g.vulnerable = true;
                    g.vulnerableUntil = now + 10_000;
                    g.eaten = false;
                }
                break;
            }
        }
        powerPellets.remove(pelletHit);

        // Win condition
        if (foods.isEmpty() && powerPellets.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    // ===== COLLISION DETECTION =====
    public boolean collision(Block a, Block b) {
        return a.x < b.x + b.width &&
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    // ===== RENDERING =====
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        // Draw walls
        for (Block wall : walls) {
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        // Draw Pac-Man
        if (pacman != null) {
            g.drawImage(pacman.image, pacman.x, pacman.y, pacman.width, pacman.height, null);
        }

        // Draw ghosts
        long now = System.currentTimeMillis();
        for (Block ghost : ghosts) {
            if (ghost.eaten) continue;

            Image img = ghost.image;
            if (ghost.vulnerable) {
                if (ghost.vulnerableUntil - now < 2000 && (now / 250) % 2 == 0) {
                    img = null; // blink
                } else {
                    img = vulnerableGhostImage;
                }
            }

            if (img != null) {
                g.drawImage(img, ghost.x, ghost.y, ghost.width, ghost.height, null);
            } else {
                // Draw white outline when blinking
                g.setColor(Color.WHITE);
                g.drawRect(ghost.x, ghost.y, ghost.width - 1, ghost.height - 1);
            }
        }

        // Draw food (dots)
        g.setColor(Color.WHITE);
        for (Block food : foods) {
            g.fillOval(food.x, food.y, food.width, food.height);
        }

        // Draw power pellets (animated)
        for (Block pellet : powerPellets) {
            Image img = ((now / 500) % 2 == 0) ? powerPelletImage : powerPelletImage2;
            g.drawImage(img, pellet.x, pellet.y, pellet.width, pellet.height, null);
        }

        // UI: Score & Lives
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        if (gameOver) {
            g.drawString("GAME OVER! Score: " + score, tileSize, tileSize);
            g.drawString("Press any key to restart", tileSize, tileSize * 2);
        } else {
            g.drawString("Lives: " + lives + "   Score: " + score, tileSize, tileSize);
        }
    }

    // ===== KEY INPUT =====
    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver) {
            // Restart
            loadMap();
            resetPositions();
            gameLoop.start();
            return;
        }

        char newDir = pacman.direction;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    newDir = 'U'; break;
            case KeyEvent.VK_DOWN:  newDir = 'D'; break;
            case KeyEvent.VK_LEFT:  newDir = 'L'; break;
            case KeyEvent.VK_RIGHT: newDir = 'R'; break;
            default: return;
        }

        if (newDir != pacman.direction) {
            pacman.updateDirection(newDir);
            // Update image
            switch (pacman.direction) {
                case 'U': pacman.image = pacManUpImage; break;
                case 'D': pacman.image = pacManDownImage; break;
                case 'L': pacman.image = pacManLeftImage; break;
                case 'R': pacman.image = pacManRightImage; break;
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}

    // ===== MAIN METHOD (for testing) =====
    public static void main(String[] args) {
        JFrame frame = new JFrame("Pac-Man");
        PacMan game = new PacMan();
        frame.add(game);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}