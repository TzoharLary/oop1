public class King extends ConcretePiece{

    public King(Player owner, String ID) {
        super(owner,ID);
    }
    @Override
    public String getType() {

        return "♔";
    }


}
