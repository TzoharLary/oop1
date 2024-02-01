public class Pawn extends ConcretePiece{






    public Pawn(Player owner, String ID) {

        super(owner, ID);
    }



    @Override
    public String getType() {
    if (getOwner().isPlayerOne()) {
        return  "♙";
    }
    else {
        return  "♟\uFE0E";
    }
    }
}


