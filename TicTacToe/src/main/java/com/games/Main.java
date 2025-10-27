package com.games;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        showModeSelector();
    }

    public static void showModeSelector() {
        SwingUtilities.invokeLater(() -> {
            Object[] options = {"Human", "Computer"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Play against a Human or the Computer?",
                    "Tic-Tac-Toe - Select Mode",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            boolean vsComputer = (choice == JOptionPane.NO_OPTION); // "Computer" = index 1

            if (choice == JOptionPane.CLOSED_OPTION) {
                System.exit(0); // Exit if user closes dialog
            }

            new TicTacToe(vsComputer);
        });
    }
}
