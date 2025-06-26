
// gui/ChessBoard.java
package gui;

import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame {
    public ChessBoard() {
        setTitle("Java Chess");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        add(new ChessPanel());
        setVisible(true);
    }
}