package com.games;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Sudoku {

    int boardwidth = 600;
    int boardHeight = 650;

    JFrame frame = new JFrame("Sudoku");

    Sudoku() {
        frame.setVisible(true);
        frame.setSize(boardwidth, boardHeight);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
    }
}
