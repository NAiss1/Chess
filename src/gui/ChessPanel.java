// ChessPanel.java
package gui;

import model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

public class ChessPanel extends JPanel {
    private final int TILE_SIZE = 80;
    private Piece[][] board;
    private int selectedX = -1, selectedY = -1;
    private boolean whiteTurn = true;
    private List<Point> legalMoves = new ArrayList<>();
    private List<String> moveHistory = new ArrayList<>();
    private Map<String, BufferedImage> pieceImages = new HashMap<>();

    public ChessPanel() {
        setPreferredSize(new Dimension(8 * TILE_SIZE + 200, 8 * TILE_SIZE));
        board = new Piece[8][8];
        loadPieceImages();
        initializePieces();

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int x = e.getX() / TILE_SIZE;
                int y = e.getY() / TILE_SIZE;

                if (x >= 8) return;

                if (selectedX == -1 && board[y][x] != null && board[y][x].isWhite() == whiteTurn) {
                    selectedX = x;
                    selectedY = y;
                    legalMoves = calculateLegalMoves(selectedX, selectedY);
                } else if (selectedX != -1) {
                    Piece moved = board[selectedY][selectedX];
                    if (moved.isValidMove(selectedX, selectedY, x, y, board)) {
                        Piece[][] clone = cloneBoard(board);
                        clone[y][x] = clone[selectedY][selectedX];
                        clone[selectedY][selectedX] = null;
                        boolean causesCheck = isInCheck(whiteTurn, clone);
                        if (!causesCheck) {
                            Piece captured = board[y][x];
                            board[y][x] = moved;
                            board[selectedY][selectedX] = null;

                            String move = (whiteTurn ? "White: " : "Black: ") + moved.toString() + " " + (char)('a'+selectedX) + (8 - selectedY) + " -> " + (char)('a'+x) + (8 - y);
                            if (captured != null) move += " x" + captured.toString();
                            moveHistory.add(move);

                            whiteTurn = !whiteTurn;
                            if (isInCheck(whiteTurn)) {
                                if (hasNoLegalMoves(whiteTurn)) {
                                    JOptionPane.showMessageDialog(null, (whiteTurn ? "White" : "Black") + " is in checkmate!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                                } else {
                                    JOptionPane.showMessageDialog(null, (whiteTurn ? "White" : "Black") + " is in check!", "Check", JOptionPane.WARNING_MESSAGE);
                                }
                            } else if (hasNoLegalMoves(whiteTurn)) {
                                JOptionPane.showMessageDialog(null, "Stalemate! Game Drawn.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                    selectedX = selectedY = -1;
                    legalMoves.clear();
                }
                repaint();
            }
        });
    }

    private void loadPieceImages() {
        try {
            BufferedImage sprite = ImageIO.read(new File("src/model/chess_pieces.png"));
            int pieceWidth = (sprite.getWidth() - 16) / 12;
            int pieceHeight = sprite.getHeight() - 480;

            String[] keys = {"wP", "wN", "wB", "wR", "wQ", "wK", "bK", "bQ", "bR", "bB", "bN", "bP"};
            for (int i = 0; i < keys.length; i++) {
                pieceImages.put(keys[i], sprite.getSubimage(8 + i * pieceWidth, 240, pieceWidth, pieceHeight));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean hasNoLegalMoves(boolean white) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board[y][x];
                if (p != null && p.isWhite() == white) {
                    for (int ny = 0; ny < 8; ny++) {
                        for (int nx = 0; nx < 8; nx++) {
                            if (p.isValidMove(x, y, nx, ny, board)) {
                                Piece[][] clone = cloneBoard(board);
                                clone[ny][nx] = clone[y][x];
                                clone[y][x] = null;
                                if (!isInCheck(white, clone)) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
    

    
    



    private void initializePieces() {
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn(false);
            board[6][i] = new Pawn(true);
        }
        board[0][0] = board[0][7] = new Rook(false);
        board[7][0] = board[7][7] = new Rook(true);
        board[0][1] = board[0][6] = new Knight(false);
        board[7][1] = board[7][6] = new Knight(true);
        board[0][2] = board[0][5] = new Bishop(false);
        board[7][2] = board[7][5] = new Bishop(true);
        board[0][3] = new Queen(false);
        board[7][3] = new Queen(true);
        board[0][4] = new King(false);
        board[7][4] = new King(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                g.setColor((row + col) % 2 == 0 ? new Color(240, 217, 181) : new Color(181, 136, 99));
                g.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (row == selectedY && col == selectedX) {
                    g.setColor(Color.YELLOW);
                    g.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    g.drawRect(col * TILE_SIZE + 1, row * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                }

                for (Point p : legalMoves) {
                    if (p.x == col && p.y == row) {
                        g.setColor(new Color(0, 255, 0, 128));
                        g.fillOval(col * TILE_SIZE + 30, row * TILE_SIZE + 30, 20, 20);
                    }
                }

                if (board[row][col] != null) {
                    String key = (board[row][col].isWhite() ? "w" : "b") + board[row][col].toString().toUpperCase();
                    BufferedImage img = pieceImages.get(key);
                    if (img != null) {
                        g.drawImage(img, col * TILE_SIZE + 10, row * TILE_SIZE + 10, TILE_SIZE - 20, TILE_SIZE - 20, null);
                    }
                }
            }
        }

        g.setColor(new Color(240, 240, 240));
        g.fillRect(8 * TILE_SIZE, 0, 200, 8 * TILE_SIZE);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("Move History:", 8 * TILE_SIZE + 10, 20);
        for (int i = 0; i < moveHistory.size(); i++) {
            g.drawString(moveHistory.get(i), 8 * TILE_SIZE + 10, 40 + i * 15);
        }

        // Draw current turn info
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(whiteTurn ? Color.DARK_GRAY : Color.BLACK);
        g.drawString("Current Turn:", 8 * TILE_SIZE + 10, 8 * TILE_SIZE - 40);
        g.setColor(whiteTurn ? new Color(60, 60, 60) : new Color(10, 10, 10));
        g.drawString(whiteTurn ? "White" : "Black", 8 * TILE_SIZE + 10, 8 * TILE_SIZE - 20);
    }


    private List<Point> calculateLegalMoves(int startX, int startY) {
        List<Point> moves = new ArrayList<>();
        Piece piece = board[startY][startX];
        if (piece == null) return moves;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (piece.isValidMove(startX, startY, x, y, board)) {
                    Piece[][] clone = cloneBoard(board);
                    clone[y][x] = clone[startY][startX];
                    clone[startY][startX] = null;
                    if (!isInCheck(whiteTurn, clone)) {
                        moves.add(new Point(x, y));
                    }
                }
            }
        }
        return moves;
    }

    private boolean isInCheck(boolean white) {
        int kingX = -1, kingY = -1;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board[y][x];
                if (p instanceof King && p.isWhite() == white) {
                    kingX = x;
                    kingY = y;
                }
            }
        }
        if (kingX == -1 || kingY == -1) return true;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = board[y][x];
                if (p != null && p.isWhite() != white) {
                    if (p.isValidMove(x, y, kingX, kingY, board)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isInCheck(boolean white, Piece[][] boardCopy) {
        int kingX = -1, kingY = -1;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = boardCopy[y][x];
                if (p instanceof King && p.isWhite() == white) {
                    kingX = x;
                    kingY = y;
                }
            }
        }
        if (kingX == -1 || kingY == -1) return true;

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Piece p = boardCopy[y][x];
                if (p != null && p.isWhite() != white) {
                    if (p.isValidMove(x, y, kingX, kingY, boardCopy)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private Piece[][] cloneBoard(Piece[][] original) {
        Piece[][] copy = new Piece[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = original[i][j];
                if (p != null) {
                    if (p instanceof Pawn) copy[i][j] = new Pawn(p.isWhite());
                    else if (p instanceof Rook) copy[i][j] = new Rook(p.isWhite());
                    else if (p instanceof Knight) copy[i][j] = new Knight(p.isWhite());
                    else if (p instanceof Bishop) copy[i][j] = new Bishop(p.isWhite());
                    else if (p instanceof Queen) copy[i][j] = new Queen(p.isWhite());
                    else if (p instanceof King) copy[i][j] = new King(p.isWhite());
                }
            }
        }
        return copy;
    }
}
