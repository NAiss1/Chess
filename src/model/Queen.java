
// model/Queen.java
package model;

public class Queen extends Piece {
    public Queen(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        return new Rook(isWhite).isValidMove(startX, startY, endX, endY, board) ||
               new Bishop(isWhite).isValidMove(startX, startY, endX, endY, board);
    }

    @Override
    public String toString() {
        return isWhite ? "Q" : "q";
    }
}
