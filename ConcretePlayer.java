public class ConcretePlayer implements Player{

    private boolean isPlayerOne;
    private int wins;

    // Constructor
    public ConcretePlayer(){

    }
    public ConcretePlayer(boolean isPlayerOne) {
        this.isPlayerOne = isPlayerOne;
        this.wins = 0;
    }
    @Override
    public boolean isPlayerOne() {
        return isPlayerOne;
    }

    @Override
    public int getWins() {
        return wins;
    }

    public void setWins() {
        this.wins++;
    }
}

