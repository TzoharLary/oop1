import java.util.ArrayList;

public class Position {
    private int x;
    private int y;

    private int numofmarched = 0;
    public ArrayList<String> marchedsquares = new ArrayList<>();

    public Position(int row, int col) {
        this.x = row;
        this.y = col;
    }

    public int getX() {
        return x;
    }

    // Getter for y
    public int getY() {
        return y;
    }

    public String toString() {
        return ("(" + getX() + getY() + ")");
    }

    public void addMarchedsquares(ConcretePiece a) {
        boolean contains = marchedsquares.contains(a.getId());
        if (!contains) {
            marchedsquares.add(a.getId());
            numofmarched++;
        }
    }


    public int getNumofmarched() {
        return numofmarched;
    }
}
