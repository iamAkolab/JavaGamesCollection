package com.games;

import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;
import java.util.Objects;
import javax.sound.sampled.*;
import javax.swing.*;

import static com.games.Main.showModeSelector;

public class TicTacToe {
    int boardWidth = 600;
    int boardHeight = 700; // 50 px for the text panel on top, 50 px for rest button

    JFrame frame = new JFrame("Tic-Tac-Toe");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JButton[][] board = new JButton[3][3];
    String playerX = "X";
    String playerO = "O";
    String currentPlayer = playerX;

    boolean gameOver = false;
    int turns = 0;

    private JButton resetButton;

    private JLabel scoreLabel;
    private int scoreX = 0;
    private int scoreO = 0;

    private int matchWinsX = 0;
    private int matchWinsO = 0;
    private static final int MATCH_WIN_THRESHOLD = 3; // Best of 5 → first to 3 wins
    private boolean matchOver = false;

    // Bottom panel for score + reset button
    JPanel bottomPanel = new JPanel(new BorderLayout());

    private JButton newMatchButton;
    private JButton backToHomeButton;

    private final boolean vsComputer;

    // Constructor now takes the mode
    public TicTacToe(boolean vsComputer) {
        this.vsComputer = vsComputer;
        initializeGame(); // Move all UI setup here
    }// Make it final — set once at startup

    private void initializeGame() {
        Color bgColor = new Color(0x1e1e2e);      // Deep navy
        Color panelColor = new Color(0x25283b);

        frame.getContentPane().setBackground(bgColor);
        textPanel.setBackground(bgColor);
        boardPanel.setBackground(panelColor);// Panel background

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.DARK_GRAY);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic-Tac-Toe");
        // Remove opaque if you want transparency over bg
        textLabel.setOpaque(false); // ← cleaner look

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        //boardPanel.setBackground(Color.DARK_GRAY);
        frame.add(boardPanel,BorderLayout.CENTER);

        // Score label
        scoreLabel = new JLabel("Player X: 0   Player O: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        scoreLabel.setForeground(new Color(0x5d5fef));
        scoreLabel.setOpaque(false); // Blend with background
        //scoreLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // Reset button
        resetButton = new JButton("Reset");
        resetButton.setFocusable(false);
        resetButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        resetButton.setBackground(new Color(0x5d5fef));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        resetButton.addActionListener(e -> resetGame());

        newMatchButton = new JButton("New Match");
        newMatchButton.setFocusable(false);
        newMatchButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        newMatchButton.setBackground(new Color(0x5d5fef));
        newMatchButton.setForeground(Color.WHITE);
        newMatchButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        newMatchButton.addActionListener(e -> newMatch());

        backToHomeButton = new JButton("Main Menu");
        backToHomeButton.setFocusable(false);
        backToHomeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backToHomeButton.setBackground(new Color(0x6a5af9)); // Slightly brighter
        backToHomeButton.setForeground(Color.WHITE);
        backToHomeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        backToHomeButton.addActionListener(e -> {
            frame.dispose(); // Close current game window
            showModeSelector(); // Show mode selector again
        });

        // Control panel with reset, new match, and home buttons
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        controlPanel.add(scoreLabel);
        controlPanel.add(resetButton);
        controlPanel.add(newMatchButton);
        controlPanel.add(backToHomeButton); // ← Add here

        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        Color tileBg = new Color(0x3a3d5a); // Muted purple-gray
        Color tileHoverBg = new Color(0x4e5173); // Lighter on hover
        Color borderColor = new Color(0x2a2d45); // Darker than tile bg

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton tile = new JButton();
                board[r][c] = tile;

                // Asymmetric border for clean grid
                // 🔥 Add border: 2px between tiles (adjust as needed)
                int top = (r == 0) ? 2 : 1;
                int left = (c == 0) ? 2 : 1;
                int bottom = (r == 2) ? 2 : 1;
                int right = (c == 2) ? 2 : 1;
                final int fTop = top, fLeft = left, fBottom = bottom, fRight = right;

                tile.setBackground(tileBg);
                tile.setOpaque(true);
                tile.setBorder(BorderFactory.createMatteBorder(fTop, fLeft, fBottom, fRight, borderColor));
                tile.setForeground(Color.WHITE);
                tile.setFont(new Font("Arial", Font.BOLD, 100));
                tile.setFocusable(false);
                tile.setContentAreaFilled(false); // Ensures no default button fill

                // Optional: Simulate "hover" with mouse listener
                tile.addMouseListener(new MouseAdapter() {
                    public void mouseEntered(MouseEvent e) {
                        if (!gameOver && !matchOver && tile.getText().isEmpty()) {
                            tile.setBackground(tileHoverBg);
                            tile.setBorder(BorderFactory.createMatteBorder(
                                    fTop, fLeft, fBottom, fRight, new Color(0x5a5d75) // Brighter border
                            ));
                        }
                    }
                    public void mouseExited(MouseEvent e) {
                        if (tile.getText().isEmpty()) {
                            tile.setBackground(tileBg);
                            tile.setBorder(BorderFactory.createMatteBorder(
                                    fTop, fLeft, fBottom, fRight, borderColor
                            ));
                        }
                    }
                });

                tile.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (gameOver || matchOver) return;
                        if (vsComputer && currentPlayer.equals(playerO)) return;

                        JButton tile = (JButton) e.getSource();
                        if (tile.getText().isEmpty()) {
                            tile.setText(currentPlayer);

                            // Set color based on player
                            if (currentPlayer.equals(playerX)) {
                                tile.setForeground(new Color(0xff6b6b)); // Coral red
                            } else {
                                tile.setForeground(new Color(0x4dccbd)); // Mint teal
                            }

                            turns++;
                            playSound("click.wav");

                            checkWinner();

                            if (!gameOver && !matchOver) {
                                currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;

                                // If playing vs computer and it's O's turn, let AI move
                                if (vsComputer && currentPlayer.equals(playerO)) {
                                    textLabel.setText("Computer is thinking...");
                                    frame.repaint();

                                    // Small delay for realism
                                    Timer timer = new Timer(600, ev -> makeAIMove());
                                    timer.setRepeats(false);
                                    timer.start();
                                } else {
                                    textLabel.setText(currentPlayer + "'s turn.");
                                }
                            }
                        }
                    }
                });

                boardPanel.add(tile);
            }
        }

//        for (int r = 0; r < 3; r++) {
//            for (int c = 0; c < 3; c++) {
//                JButton tile = new JButton();
//                board[r][c] = tile;
//                boardPanel.add(tile);
//
//                // Beautiful idle state
//                tile.setBackground(new Color(0x3a3d5a)); // Muted purple-gray
//                tile.setForeground(Color.WHITE);
//                tile.setOpaque(true); // Important: must be true for background to show
//                tile.setFont(new Font("Arial", Font.BOLD, 100)); // Large = feels bold
//                tile.setFocusable(false);
//                //tile.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); // More padding
//
//                // 🔥 Add border: 2px between tiles (adjust as needed)
//                // Top, Left, Bottom, Right
//                int top = (r == 0) ? 2 : 1;     // Thicker top edge
//                int left = (c == 0) ? 2 : 1;    // Thicker left edge
//                int bottom = (r == 2) ? 2 : 1;  // Thicker bottom
//                int right = (c == 2) ? 2 : 1;   // Thicker right
//
//                Color borderColor = new Color(0x2a2d45); // Darker than tile bg
//                tile.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, borderColor));
//
//                tile.setContentAreaFilled(false); // Optional: helps with custom look
//
//                // Optional: Simulate "hover" with mouse listener
//                tile.addMouseListener(new MouseAdapter() {
//                    public void mouseEntered(MouseEvent e) {
//                        if (!gameOver && !matchOver && tile.getText().isEmpty()) {
//                            tile.setBackground(new Color(0x4e5173)); // Lighter on hover
//                            tile.setBorder(BorderFactory.createMatteBorder(
//                                    top, left, bottom, right, new Color(0x5a5d75) // Brighter border
//                            ));
//                        }
//                    }
//                    public void mouseExited(MouseEvent e) {
//                        if (tile.getText().isEmpty()) {
//                            tile.setBackground(new Color(0x3a3d5a)); // Back to idle
//                            tile.setBorder(BorderFactory.createMatteBorder(
//                                    top, left, bottom, right, borderColor
//                            ));
//                        }
//                    }
//                });
//
//                tile.addActionListener(new ActionListener() {
//                    public void actionPerformed(ActionEvent e) {
//                        if (gameOver || matchOver) return;
//                        if (vsComputer && currentPlayer.equals(playerO)) return;
//
//                        JButton tile = (JButton) e.getSource();
//                        if (tile.getText().isEmpty()) {
//                            tile.setText(currentPlayer);
//
//                            // Set color based on player
//                            if (currentPlayer.equals(playerX)) {
//                                tile.setForeground(new Color(0xff6b6b)); // Coral red
//                            } else {
//                                tile.setForeground(new Color(0x4dccbd)); // Mint teal
//                            }
//
//                            turns++;
//                            playSound("click.wav");
//
//                            checkWinner();
//
//                            if (!gameOver && !matchOver) {
//                                currentPlayer = currentPlayer.equals(playerX) ? playerO : playerX;
//
//                                // If playing vs computer and it's O's turn, let AI move
//                                if (vsComputer && currentPlayer.equals(playerO)) {
//                                    textLabel.setText("Computer is thinking...");
//                                    frame.repaint();
//
//                                    // Small delay for realism
//                                    Timer timer = new Timer(600, ev -> makeAIMove());
//                                    timer.setRepeats(false);
//                                    timer.start();
//                                } else {
//                                    textLabel.setText(currentPlayer + "'s turn.");
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }

        textLabel.setText(currentPlayer + "'s turn.");
        frame.setVisible(true);
    }

    private void updateScore(String winner) {
        if (Objects.equals(winner, playerX)) {
            scoreX++;
            matchWinsX++;
        } else if (Objects.equals(winner, playerO)) {
            scoreO++;
            matchWinsO++;
        }
        scoreLabel.setText("Player X: " + scoreX + "   Player O: " + scoreO);

        // Check for match win (best of 5 → first to 3)
        if (matchWinsX >= MATCH_WIN_THRESHOLD || matchWinsO >= MATCH_WIN_THRESHOLD) {
            matchOver = true;
            String matchWinner = (matchWinsX >= MATCH_WIN_THRESHOLD) ? "X" : "O";
            textLabel.setText("Player " + matchWinner + " wins the match!");
            disableBoard();
            resetButton.setVisible(false); // ← Hide reset round
        }
    }

    private void disableBoard() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                board[r][c].setEnabled(false);
            }
        }
    }

    private void resetGame() {
        if (matchOver) return; // Don't allow round reset after match ends

        resetButton.setVisible(true);

        currentPlayer = playerX;
        gameOver = false;
        turns = 0;
        textLabel.setText(currentPlayer + "'s turn.");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton tile = board[i][j];
                tile.setText("");
                tile.setBackground(Color.darkGray);
                tile.setForeground(Color.white);
                tile.setEnabled(true); // Re-enable in case it was disabled
            }
        }
    }

    private void newMatch() {
        // Reset everything
        scoreX = 0;
        scoreO = 0;
        matchWinsX = 0;
        matchWinsO = 0;
        matchOver = false;
        scoreLabel.setText("Player X: 0   Player O: 0");
        resetButton.setVisible(true);
        resetGame(); // Also resets board and enables buttons
    }

    void checkWinner() {
        //horizontal
        for (int r = 0; r < 3; r++) {
            if (Objects.equals(board[r][0].getText(), "")) continue;

            if (board[r][0].getText().equals(board[r][1].getText()) &&
                    Objects.equals(board[r][1].getText(), board[r][2].getText())) {
                String winner = board[r][0].getText();
                for (int i = 0; i < 3; i++) {
                    setWinner(board[r][i], winner);
                }
                gameOver = true;
                updateScore(winner);
                textLabel.setText(winner + " wins! Press 'Reset'");
                return;
            }
        }

        //vertical
        for (int c = 0; c < 3; c++) {
            if (Objects.equals(board[0][c].getText(), "")) continue;

            if (Objects.equals(board[0][c].getText(), board[1][c].getText()) &&
                    Objects.equals(board[1][c].getText(), board[2][c].getText())) {
                String winner = board[0][c].getText();
                for (int i = 0; i < 3; i++) {
                    setWinner(board[i][c], winner);
                }
                gameOver = true;
                updateScore(winner);
                textLabel.setText(winner + " wins! Press 'Reset'");
                return;
            }
        }

        //diagonally
        if (Objects.equals(board[0][0].getText(), board[1][1].getText()) &&
                Objects.equals(board[1][1].getText(), board[2][2].getText()) &&
                !Objects.equals(board[0][0].getText(), "")) {
            String winner = board[0][0].getText();
            for (int i = 0; i < 3; i++) {
                setWinner(board[i][i], winner);
            }
            gameOver = true;
            updateScore(winner);
            textLabel.setText(winner + " wins! Press 'Reset'");
            return;
        }

        //anti-diagonally
        if (Objects.equals(board[0][2].getText(), board[1][1].getText()) &&
                Objects.equals(board[1][1].getText(), board[2][0].getText()) &&
                !Objects.equals(board[0][2].getText(), "")) {
            String winner = board[0][2].getText();
            setWinner(board[0][2], winner);
            setWinner(board[1][1], winner);
            setWinner(board[2][0], winner);
            gameOver = true;
            updateScore(winner);
            return;
        }

        if (turns == 9) {
            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 3; c++) {
                    setTie(board[r][c]);
                }
            }
            gameOver = true;
            textLabel.setText("Tie! Press 'Reset' to continue");
            playSound("tie.wav");
        }
    }

//    void setWinner(JButton tile, String winner) {
//        if (winner.equals(playerX)) {
//            tile.setBackground(new Color(0x5e3a3a)); // Deep red bg
//            tile.setForeground(new Color(0xffa8a8)); // Light red text
//        } else {
//            tile.setBackground(new Color(0x3a5e52)); // Deep teal bg
//            tile.setForeground(new Color(0xa8e6d9)); // Light teal text
//        }
//        tile.setFont(new Font("Arial", Font.BOLD, 100));
//
//        if (!gameOver) { // Only play once
//            playSound("win.wav");
//        }
//    }

    void setWinner(JButton tile, String winner) {
        // Set background to a subtle dark highlight
        tile.setBackground(new Color(0x2a2d45)); // Darker than board

        // Color the text based on who won
        if (winner.equals(playerX)) {
            // Human (X) won → GREEN
            tile.setForeground(new Color(0x4ade80)); // Bright green (e.g., #4ade80)
            tile.setFont(new Font("Arial", Font.BOLD, 100));
        } else if (winner.equals(playerO)) {
            // Computer (O) won → RED
            tile.setForeground(new Color(0xf87171)); // Soft red (e.g., #f87171)
            tile.setFont(new Font("Arial", Font.BOLD, 100));
        }

        // Play sound only once (optional guard)
        if (!gameOver) {
            playSound("win.wav");
        }
    }

    void setTie(JButton tile) {
        tile.setBackground(new Color(0x5e4a3a)); // Deep amber
        tile.setForeground(new Color(0xffe0b2)); // Light amber
        tile.setFont(new Font("Arial", Font.BOLD, 100));
    }

//    void setWinner(JButton tile, String winner) {
//        tile.setForeground(Color.green);
//        tile.setBackground(Color.gray);
//        textLabel.setText(winner + " is the winner!");
//    }
//
//    void setTie(JButton tile) {
//        tile.setForeground(Color.orange);
//        tile.setBackground(Color.gray);
//        textLabel.setText("Tie!");
//    }

    private void makeAIMove() {
        if (gameOver || matchOver) return;

        // 1. Try to win
        JButton winMove = findWinningMove(playerO);
        if (winMove != null) {
            winMove.setText(playerO);
            turns++;
            playSound("click.wav");
            checkWinner();
            if (!gameOver) {
                currentPlayer = playerX;
                textLabel.setText(currentPlayer + "'s turn.");
            }
            return;
        }

        // 2. Block opponent win
        JButton blockMove = findWinningMove(playerX);
        if (blockMove != null) {
            blockMove.setText(playerO);
            turns++;
            playSound("click.wav");
            checkWinner();
            if (!gameOver) {
                currentPlayer = playerX;
                textLabel.setText(currentPlayer + "'s turn.");
            }
            return;
        }

        // 3. Take center if available
        if (board[1][1].getText().isEmpty()) {
            board[1][1].setText(playerO);
            turns++;
            playSound("click.wav");
            checkWinner();
            if (!gameOver) {
                currentPlayer = playerX;
                textLabel.setText(currentPlayer + "'s turn.");
            }
            return;
        }

        // 4. Take a random corner or side
        java.util.List<JButton> emptyTiles = new java.util.ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c].getText().isEmpty()) {
                    emptyTiles.add(board[r][c]);
                }
            }
        }
        if (!emptyTiles.isEmpty()) {
            java.util.Random rand = new java.util.Random();
            JButton randomTile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
            randomTile.setText(playerO);
            turns++;
            playSound("click.wav");
            checkWinner();
            if (!gameOver) {
                currentPlayer = playerX;
                textLabel.setText(currentPlayer + "'s turn.");
            }
        }
    }

    private JButton findWinningMove(String player) {
        // Check rows
        for (int r = 0; r < 3; r++) {
            int count = 0;
            JButton empty = null;
            for (int c = 0; c < 3; c++) {
                String text = board[r][c].getText();
                if (text.equals(player)) count++;
                else if (text.isEmpty()) empty = board[r][c];
            }
            if (count == 2 && empty != null) return empty;
        }

        // Check columns
        for (int c = 0; c < 3; c++) {
            int count = 0;
            JButton empty = null;
            for (int r = 0; r < 3; r++) {
                String text = board[r][c].getText();
                if (text.equals(player)) count++;
                else if (text.isEmpty()) empty = board[r][c];
            }
            if (count == 2 && empty != null) return empty;
        }

        // Diagonal 1
        int count = 0;
        JButton empty = null;
        for (int i = 0; i < 3; i++) {
            String text = board[i][i].getText();
            if (text.equals(player)) count++;
            else if (text.isEmpty()) empty = board[i][i];
        }
        if (count == 2 && empty != null) return empty;

        // Diagonal 2
        count = 0;
        empty = null;
        for (int i = 0; i < 3; i++) {
            String text = board[i][2 - i].getText();
            if (text.equals(player)) count++;
            else if (text.isEmpty()) empty = board[i][2 - i];
        }
        if (count == 2 && empty != null) return empty;

        return null;
    }

    private void playSound(String soundFile) {
        try {
            // Try to load from resources (e.g., src/main/resources/sounds/)
            InputStream in = getClass().getResourceAsStream("/sounds/" + soundFile);
            if (in == null) return; // Skip if sound not found

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(in);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (Exception e) {
            // Silently fail if sound not available (e.g., in IDE without resources)
            // System.out.println("Sound error: " + e.getMessage());
        }
    }
//    On tile click: playSound("click.wav");
//    On win: playSound("win.wav");
//    On tie: playSound("tie.wav");
}
