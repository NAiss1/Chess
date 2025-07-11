
// model/King.java
package model;

public class King extends Piece {
    public King(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int dx = Math.abs(startX - endX);
        int dy = Math.abs(startY - endY);
        return dx <= 1 && dy <= 1 && (board[endY][endX] == null || board[endY][endX].isWhite() != isWhite);
    }

    @Override
    public String toString() {
        return isWhite ? "K" : "k";
    }
}