package model;

public class Pawn extends Piece {
    public Pawn(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        int direction = isWhite ? -1 : 1;
        if (startX == endX && board[endY][endX] == null) {
            if (endY - startY == direction) return true;
            if ((isWhite && startY == 6 || !isWhite && startY == 1) && endY - startY == 2 * direction && board[startY + direction][startX] == null) return true;
        }
        if (Math.abs(startX - endX) == 1 && endY - startY == direction && board[endY][endX] != null && board[endY][endX].isWhite() != isWhite) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return isWhite ? "P" : "p";
    }
}