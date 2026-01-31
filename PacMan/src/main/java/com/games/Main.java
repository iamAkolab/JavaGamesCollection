package com.games;

import javax.swing.*;
//import java.awt.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::showSelectionDialog);
    }

    private static void showSelectionDialog() {
        Object[] options = {"Classic Pac-Man", "Enhanced Pac-Man", "Exit"};

        int choice = JOptionPane.showOptionDialog(
                null,
                "Choose your Pac-Man version:",
                "Pac-Man Game Selector",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        JFrame frame = createBaseFrame();

        switch (choice) {
            case 0: // Classic
                OldPacMan oldGame = new OldPacMan();
                frame.setTitle("Classic Pac-Man");
                frame.add(oldGame);
                break;

            case 1: // Enhanced
                PacMan newGame = new PacMan();
                frame.setTitle("Enhanced Pac-Man");
                frame.add(newGame);
                break;

            default: // Cancel/Exit
                System.exit(0);
        }

        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JFrame createBaseFrame() {

        int rowCount = 21;
        int columnCount = 19;
        int tileSize = 32;
        int boardWidth = columnCount * tileSize;
        int boardHeight = rowCount * tileSize;

        JFrame frame = new JFrame("Pac Man");
        //frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        return frame;
    }
    
}
