package com.games;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class TicTacToe {
    int boardwidth = 600;
    int boardHeight = 650; // 50 px for the text panel on top

    JFrame frame = new JFrame();
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();

    TicTacToe() {
        frame.setVisible(true);
        frame.setSize(boardwidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setBackground(Color.DARK_GRAY);
        textLabel.setForeground(Color.WHITE);
        textLabel.setFont(new Font("Arial", Font.BOLD, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Tic-Tac-Toe");
        textLabel.setOpaque(true);

    }
}
