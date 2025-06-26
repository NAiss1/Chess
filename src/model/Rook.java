package model;

public class Rook extends Piece {
    public Rook(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public boolean isValidMove(int startX, int startY, int endX, int endY, Piece[][] board) {
        if (startX != endX && startY != endY) return false;

        int xDir = Integer.compare(endX, startX);
        int yDir = Integer.compare(endY, startY);

        int x = startX + xDir;
        int y = startY + yDir;
        while (x != endX || y != endY) {
            if (board[y][x] != null) return false;
            x += xDir;
            y += yDir;
        }

        return board[endY][endX] == null || board[endY][endX].isWhite() != isWhite;
    }

    @Override
    public String toString() {
        return isWhite ? "R" : "r";
    }
}