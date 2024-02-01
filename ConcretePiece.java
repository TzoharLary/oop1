import java.util.ArrayList;

import static javax.swing.text.html.HTML.Tag.U;

public abstract class ConcretePiece implements Piece {

    private Player Player1;


    private String id;
    private int NumOfEats = 0;
    private int numofsquares = 0;




    public ArrayList<Position> Moves = new ArrayList<>();
    private int numofmvoes = Moves.size();

    private int Winner_is_p1 = 0;


    public ConcretePiece(Player owner, String ID) {
        this.Player1 = owner;
        this.id = ID;
        this.NumOfEats = 0;

    }

    public void Eat() {
        NumOfEats++;
    }

    public int getNumOfEats() {
        return NumOfEats;
    }

    @Override
    public Player getOwner() {
        if (Player1 == null) {
            // Handle the null case appropriately, e.g., throw an exception or return a default value
            return null; // Or throw a custom exception
        } else {
            return Player1;
        }
    }

    public void setWinner_is_p1() {
            Winner_is_p1++;


    }

    public int getWinner_is_p1() {
        return Winner_is_p1;
    }


    @Override
    public String getType() {

        return null;
    }

    public String getId() {
        return id;
    }

    public int getNumofmvoes() {
        return numofmvoes;
    }

    public void addMoves(Position b) {
        Moves.add(b);
        numofmvoes++;
    }

    public void addNumofsquares(Position a, Position b){
        if (a.getX()==b.getX()){
            numofsquares = numofsquares + (Math.abs(b.getY()-a.getY()));
        }
        if (a.getY()==b.getY()){
            numofsquares = numofsquares + (Math.abs(b.getX()-a.getX()));
        }
    }

    public int getNumofsquares(){
        return numofsquares;
    }


    public void addMoves(int x, int y) {
        Position p = new Position(x, y);
        Moves.add(p);
        numofmvoes++;
    }

    public String toString() {
        return id;
    }

    public String getMovesHistory() {
        StringBuilder movesHistory = new StringBuilder();
        for (Position move : Moves) {
            movesHistory.append("(").append(move.getX()).append(", ").append(move.getY()).append("), ");

        }
        // Remove the trailing comma and space
        if (movesHistory.length() > 0) {
            movesHistory.setLength(movesHistory.length() - 2);
        }
        return movesHistory.toString();
    }


}



