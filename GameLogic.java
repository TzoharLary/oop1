import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class GameLogic implements PlayableLogic {
    int size = 11;
    public Piece[][] Game = new Piece[size][size];
    private Piece tmp;
    private int pwin;
    private boolean Winner_is_p1 = true;

    private boolean King_is_die = false;

    ArrayList<ConcretePiece> p1Piece = new ArrayList<>();
    ArrayList<ConcretePiece> p2Piece = new ArrayList<>();
    ArrayList<ConcretePiece> AllPiece = new ArrayList<>();

    ArrayList<ConcretePiece> eatenpieces = new ArrayList<>();

    ArrayList<Position> posforcom = new ArrayList<>();


    Position[][] positions = new Position[size][size];

    private boolean isGameFinished = false;
//    private ConcretePiece tmp2;

    ConcretePlayer p1 = new ConcretePlayer(false);
    ConcretePlayer p2 = new ConcretePlayer(true);
    // if t true its p2 turn
    private boolean t;

    public GameLogic() {

        reset();


    }


    @Override
    public boolean move(Position a, Position b) {
        //if p1 turn, we avoid all parts of p2 to play
        if (!t) {
            // check who's the owner of the piece at a position
            tmp = getPieceAtPosition(a);
            if (tmp.getOwner() == p2) {
                return false;
            }
        }
        //if p2 turn, we avoid all parts of p1 to play
        if (t) {
            // check who's the owner of the piece at a position
            tmp = getPieceAtPosition(a);
            if (tmp.getOwner() == p1) {
                return false;
            }
        }


        //first condition: 0<=a,b<size
        if ((0 > a.getX()) || (0 > a.getY()) || (0 > b.getX()) || (0 > b.getY())) {
            return false;
        }
        if ((size < a.getX()) || (size < a.getY()) || (size < b.getX()) || (size < b.getY())) {
            return false;
        }
        //second condition: if the piece stays in its place
        if (a.getX() == b.getX() && a.getY() == b.getY()) {
            return false;
        }
        // third condition: if Pawn, he not allow to get to corners
        if (!Game[a.getX()][a.getY()].getType().equals("♔")) {
            if ((b.getX() == 0 && b.getY() == 0) || (b.getX() == 0 && b.getY() == 10) || (b.getX() == 10 && b.getY() == 0) || (b.getX() == 10 && b.getY() == 10)) {
                return false;
            }
        }


        //third condition: we allow just a straight movement
        if (a.getX() == b.getX() || a.getY() == b.getY()) {
            //check if the movement is at y coordinate
            if (a.getX() == b.getX()) {
                //check if the destination is up from the source location
                if (b.getY() < a.getY()) {
                    //check if the way to the destination is empty
                    for (int i = a.getY() - 1; i >= b.getY(); i--) {
                        if (!isEmpty(a.getX(), i)) {
                            return false;
                        }
                    }
                }
                //check if the destination is down from the source location
                if (b.getY() > a.getY()) {
                    //check if the way to the destination is empty
                    for (int i = a.getY() + 1; i <= b.getY(); i++) {
                        if (!isEmpty(a.getX(), i)) {
                            return false;
                        }
                    }
                }
            }
            //check if the movement is at x coordinate
            if (a.getY() == b.getY()) {
                //check if the destination is right from the source location
                if (b.getX() > a.getX()) {
                    //check if the way to the destination is empty
                    for (int i = a.getX() + 1; i <= b.getX(); i++) {
                        if (!isEmpty(i, a.getY())) {
                            return false;
                        }
                    }
                }


                //check if the destination is left from the source location
                if (b.getX() < a.getX()) {
                    //check if the way to the destination is empty
                    for (int i = a.getX() - 1; i >= b.getX(); i--) {
                        if (!isEmpty(i, a.getY())) {
                            return false;
                        }
                    }
                }
            }
        }
        // else for any move that is not straight
        else {
            return false;
        }
        // moving the piece to b position + change second player turn + win\loss

        ((ConcretePiece) this.Game[a.getX()][a.getY()]).addNumofsquares(a, b);
//        System.out.println(((ConcretePiece) this.Game[a.getX()][a.getY()]).getNumofsquares());


        // move the piece to b
        Game[b.getX()][b.getY()] = this.getPieceAtPosition(a);

        //  make position a to null
        Game[a.getX()][a.getY()] = null;

        //adding a new move for piece
        ConcretePiece tmp3 = (ConcretePiece) Game[b.getX()][b.getY()];
        tmp3.addMoves(b);

        getPos(b).addMarchedsquares(tmp3);
//        System.out.println(getPos(b).getNumofmarched());


        Kingiswin(b, ((ConcretePiece) this.Game[b.getX()][b.getY()]));

        // change player turn
//    boolean t = isSecondPlayerTurn();
        t = !t;


        //eat
        if (t) {
            if (getPieceAtPosition(b) instanceof Pawn) {

                //  if (getPieceAtPosition(b).getType().equals("♟\uFE0E") || getPieceAtPosition(b).getType().equals("♙")){
                Attack(Game[b.getX()][b.getY()], b, t);
            }
        }
        if (!t) {
            if (getPieceAtPosition(b) instanceof Pawn) {

                //      if (getPieceAtPosition(b).getType().equals("♟\uFE0E") || getPieceAtPosition(b).getType().equals("♙")) {
                Attack(Game[b.getX()][b.getY()], b, !t);
            }
        }


        //  win/loss


        // if king die


        return true;
    }

    //should it be written in position class???
    public boolean isEmpty(int x, int y) {
        return Game[x][y] == null;
    }

    public void atckedgehelp(Piece piece, Position b, int x, int y, Position bxy) {
        if (edge(bxy)) {
            Position bx = new Position(x, y);
            // if the next is not null and belong to other player
            if (getPieceAtPosition(bx) != null) {
                Piece def = getPieceAtPosition(bx);
                if (def.getOwner() != piece.getOwner()) {
                    Game[bx.getX()][bx.getY()]=null;
                    ((ConcretePiece) piece).Eat();
                }
            }
        }
    }

    public void atckedge(Piece piece, Position b) {
// i need to check if attacker 8,0||10 and the defender 9,0||10 or at 2,0||10 and De 1,||10 or at 0||10,8 and de 0||10,9 or 0||10,2 and de 0||10,1

        Position bx1 = new Position(b.getX() + 2, b.getY());
        Position bx2 = new Position(b.getX() - 2, b.getY());
        Position by1 = new Position(b.getX(), b.getY() + 2);
        Position by2 = new Position(b.getX(), b.getY() - 2);
        atckedgehelp(piece, b, b.getX() + 1, b.getY(), bx1);
        atckedgehelp(piece, b, b.getX() - 1, b.getY(), bx2);
        atckedgehelp(piece, b, b.getX(), b.getY() + 1, by1);
        atckedgehelp(piece, b, b.getX(), b.getY() - 1, by2);


    }

    //func for checking attack
    public void Attack(Piece piece, Position b, boolean t) {
        // Check for attack possibilities in all directions
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                // Skip the current position
                if (dx == dy || dx == -dy) {
                    continue;
                }
                // if bx/by = 10|0 and bx+1/by+1 = 11|-1
                if ((b.getX() + dx) > 10 || (b.getY() + dy) > 10 || (b.getX() + dx) < 0 || (b.getY() + dy) < 0) {
                    continue;
                }
                // check +-1 of any direction not null
                if (Game[b.getX() + dx][b.getY() + dy] != null) {
                    //if the King in danger
                    if (Game[b.getX() + dx][b.getY() + dy].getType().equals("♔")) {
                        Piece king = Game[b.getX() + dx][b.getY() + dy];
                        Position n = new Position(b.getX() + dx, b.getY() + dy);
                        AttackKing(b.getX() + dx, b.getY() + dy, n);
                        if (King_is_die) {
                            ((ConcretePiece) Game[b.getX()][b.getY()]).Eat();
//                            System.out.println(((ConcretePiece) Game[b.getX()][b.getY()]).getNumOfEats());
//                            addeat(king);
                            reset();
                        }
                        continue;
                    }
                    //if Pawn on danger
                    if (Game[b.getX() + dx][b.getY() + dy].getType().equals("♙") || Game[b.getX() + dx][b.getY() + dy].getType().equals("♟\uFE0E")) {
                        // Check if the piece is owned by the opponent
                        if (Game[b.getX()][b.getY()] == null) {
                            continue;
                        } else {
                            if (Game[b.getX() + dx][b.getY() + dy].getOwner() != Game[b.getX()][b.getY()].getOwner()) {
                                atckedge(piece,b);

                                // if bx/by = 9|1 and bx+1/by+1 = 10|0 and bx+2/by+2=11|-1
                                if ((b.getX() + 2 * dx) > 10 || (b.getY() + 2 * dy) > 10 || (b.getX() + 2 * dx) < 0 || (b.getY() + 2 * dy) < 0) {
                                    addeat(Game[b.getX() + dx][b.getY() + dy]);
                                    Game[b.getX() + dx][b.getY() + dy] = null;
                                    ((ConcretePiece) piece).Eat();
//                                    System.out.println(((ConcretePiece) piece).getNumOfEats());

                                    continue;
                                }

                                if (Game[b.getX() + 2 * dx][b.getY() + 2 * dy] != null) {
                                    // Check if the next piece is owned by the current player
                                    if (Game[b.getX() + 2 * dx][b.getY() + 2 * dy].getOwner() == Game[b.getX()][b.getY()].getOwner()) {
                                        Position bt = new Position(b.getX() + 2 * dx, b.getY() + 2 * dy);
                                        if (getPieceAtPosition(bt).getType().equals("♟\uFE0E") || getPieceAtPosition(bt).getType().equals("♙")) {
                                            // Perform the attack
                                            addeat(Game[b.getX() + dx][b.getY() + dy]);
                                            Game[b.getX() + dx][b.getY() + dy] = null;
                                            ((ConcretePiece) piece).Eat();
//                                            System.out.println(((ConcretePiece) piece).getNumOfEats());

                                        }


                                    }
                                }
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public Position getPos(Position p) {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (p.getX() == i && p.getY() == j) {
                    return positions[i][j];
                }
            }
        }
        return null;
    }

    @Override
    public Piece getPieceAtPosition(Position position) {
        int x = position.getX();
        int y = position.getY();
        // Check if the specified position is within the board boundaries
        if (x >= 0 && x < size && y >= 0 && y < size) {
            return Game[x][y];
        } else {
            // Handle cases where the position is outside the board boundaries
            return null;
        }
    }

    public void AttackKing(int KingX, int KingY, Position n) {

        int surroundKing = 0;
        boolean Isthree = false;
        for (int dx = -1; dx < 2; dx++) {
            for (int dy = -1; dy < 2; dy++) {
                // Skip the current position
                if (dx == dy || dx == -dy) {
                    continue;
                }
                // if bx/by = 10|0 and bx+1/by+1 = 11|-1
                if (KingX > 10 || KingY > 10 || KingX < 0 || KingY < 0) {
                    continue;
                }

                if (Game[KingX][KingY] != null) {
                    // if the side is out of the limits continue
                    if ((KingX + dx) > 10 || (KingY + dy) > 10 || (KingX + dx) < 0 || (KingY + dy) < 0) {
                        continue;
                    }
                    // if the king is on  one of the sides of the board
                    if (Kingoneside(n, KingX, KingY) && !Isthree) {
                        // continue the iteration of x>10 || x<0
                        if (Game[KingX + dx][KingY + dy] == null) {
                            continue;
                        }
                        // check how much opponents near the king
                        if (Game[KingX][KingY].getOwner() != Game[KingX + dx][KingY + dy].getOwner()) {
                            surroundKing++;
                        }
                        Isthree = true;
                        continue;
                    }

                    if (Game[KingX + dx][KingY + dy] != null) {
                        if (Game[KingX][KingY].getOwner() != Game[KingX + dx][KingY + dy].getOwner()) {
                            surroundKing++;
                            if (!Kingoneside(n, KingX, KingY)) {
                                Isthree = false;
                            }
                        }
                    }
                }
            }
        }
        if (Isthree && surroundKing == 3) {
            p2.setWins();
            isGameFinished = true;
            Winner_is_p1 = false;
            King_is_die = true;
            for (ConcretePiece piece : p2Piece) {
                piece.setWinner_is_p1();
            }

            EndGame();
        }
        if (surroundKing == 4) {
            p2.setWins();
            isGameFinished = true;
            Winner_is_p1 = false;
            King_is_die = true;
            for (ConcretePiece piece : p2Piece) {
                piece.setWinner_is_p1();
            }

            EndGame();
        }

    }


    public Boolean Kingoneside(Position b, int KingX, int KingY) {
        if (KingX == 10 || KingY == 10 || KingX == 0 || KingY == 0) {

            return true;
        } else return false;
    }


    public void Kingiswin(Position b, ConcretePiece king) {
        if (king.getType().equals("♔")) {
            if (edge(b)) {
                p1.setWins();
                isGameFinished = true;
                Winner_is_p1 = true;
                King_is_die = false;
                for (ConcretePiece piece : p1Piece) {
                    piece.setWinner_is_p1();
                }
                EndGame();
//                reset();
            }
        }
    }

    public boolean edge(Position b) {
        if (b.getX() == 10 && b.getY() == 10) {
            return true;
        }
        if (b.getX() == 0 && b.getY() == 10) {
            return true;
        }
        if (b.getX() == 10 && b.getY() == 0) {
            return true;
        }
        if (b.getX() == 0 && b.getY() == 0) {
            return true;
        }
        return false;
    }

    @Override
    public Player getFirstPlayer() {
        return p1;
    }

    public void addeat(Piece piece) {
        eatenpieces.add((ConcretePiece) piece);
    }

    public void asterisk() {
        System.out.println("***************************************************************************");

    }

    public void EndGame() {

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (positions[i][j].getNumofmarched() > 1) {
                    posforcom.add(positions[i][j]);
                }
            }
        }

//        AllPiece.addAll(eatenpieces);


        // comparator for winplayer

        Comparator<ConcretePiece> by_winner = Comparator.comparingInt(ConcretePiece::getWinner_is_p1);
        // comparator - moves

        Comparator<ConcretePiece> by_moves = Comparator.comparingInt(ConcretePiece::getNumofmvoes);


        // comparator - id
        Comparator<ConcretePiece> by_id2 = Comparator.comparing(piece -> piece.getId().substring(1));


        Comparator<ConcretePiece> by_id = Comparator.comparing(ConcretePiece::toString
                , Comparator.comparingInt((String id) -> id.charAt(0)).thenComparingInt(id -> Integer.parseInt(id.substring(1)))
        );
        Comparator<ConcretePiece> by_id3 = Comparator.comparingInt(piece -> Integer.parseInt(piece.getId().substring(1)));


        Comparator<ConcretePiece> by_winner_then_moves_id = Comparator
                .comparing(ConcretePiece::getWinner_is_p1, Comparator.reverseOrder()) // Prioritize winner (P1 first if Winner_is_p1 is true)
                .thenComparing(by_moves) // Then sort by moves
                .thenComparing(by_id3); // Finally, sort by ID

// First task


        // comparator numofeats

        Comparator<ConcretePiece> by_numofeats = Comparator.comparingInt(ConcretePiece::getNumOfEats).reversed();

        // comparator for by_numofeats_id_winplayer
        Comparator<ConcretePiece> by_numofeats_id_winplayer = by_numofeats.thenComparing(by_id3).thenComparing(by_winner.reversed());


        //comparator numofsquars
        Comparator<ConcretePiece> by_numofsquars = Comparator.comparingInt(ConcretePiece::getNumofsquares).reversed();


        //comparator by_numofsquares_id_winplayer
        Comparator<ConcretePiece> by_numofsquares_id_winplayer = by_numofsquars.thenComparing(by_id3).thenComparing(by_winner.reversed());


        //comparator by_numofmarchedsquared
        Comparator<Position> by_numofmarchedsquared = Comparator.comparingInt(Position::getNumofmarched).reversed();

        //comparator by_x
        Comparator<Position> by_x = Comparator.comparingInt(Position::getX);

        //comparator by_y
        Comparator<Position> by_y = Comparator.comparingInt(Position::getY);

        //comparator by_numofmarchedsquared_x_y
        Comparator<Position> by_numofmarchedsquared_x_y = by_numofmarchedsquared.thenComparing(by_x).thenComparing(by_y);


        // check numofmoves sort
//        Collections.sort(AllPiece, by_moves);
//        System.out.println("after sorting moves");
//
//        for (ConcretePiece piece : AllPiece) {
//            if (piece.getNumofmvoes() > 1) {
//                System.out.println(piece.getId() + ": " + piece.getMovesHistory() + piece.getNumofmvoes());
//            }
//        }

        //check  id
//        Collections.sort(AllPiece, by_id);
//        System.out.println("after sorting id");
//
//        for (ConcretePiece piece : AllPiece) {
//            if (piece.getNumofmvoes() > 0) {
//                System.out.println(piece.getId());
//            }
//        }

        //first task

        Collections.sort(AllPiece, by_winner_then_moves_id);
//        System.out.println("after sorting by_winner_then_moves_id");

        for (ConcretePiece piece : AllPiece) {
            if (piece.getNumofmvoes() > 1) {
                System.out.println(piece.getId() + ": [" + piece.getMovesHistory() + "]");
            }
        }
        asterisk();
        //second task
        Collections.sort(AllPiece, by_numofeats_id_winplayer);
//        System.out.println("after sorting by_numofeats_id_winplayer");

        for (ConcretePiece piece : AllPiece) {
            if (piece.getNumOfEats() > 0) {
                System.out.println(piece.getId() + ": " + piece.getNumOfEats() + " kills");
            }
        }
        asterisk();

        //  third task
        Collections.sort(AllPiece, by_numofsquares_id_winplayer);
//        System.out.println("after sorting by_numofsquares_id_winplayer");

        for (ConcretePiece piece : AllPiece) {
            if (piece.getNumofsquares() > 0) {
                System.out.println(piece.getId() + ": " + piece.getNumofsquares() + " squares");
            }
        }
        asterisk();

        // fourth task
        Collections.sort(posforcom, by_numofmarchedsquared_x_y);
//        System.out.println("after sorting by_numofsquares_id_winplayer");

        for (Position position : posforcom) {
            System.out.println("(" + position.getX() + ", " + position.getY() + ")" + position.getNumofmarched() + " pieces");
        }
        asterisk();

    }

    @Override
    public Player getSecondPlayer() {
        return p2;
    }

    @Override
    public boolean isGameFinished() {


        return isGameFinished;
    }

    @Override
    public boolean isSecondPlayerTurn() {

        return !t;
    }


    @Override
    public void reset() {
        King_is_die = false;
        isGameFinished = false;
        Winner_is_p1 = true;


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Game[i][j] = null;
                positions[i][j] = new Position(i, j);

            }
        }

        ConcretePiece p;


  

        // setting pieces locations and id, and adding to appropriate ArrayLists
        for (int i = 3; i < 8; i++) {
            this.Game[i][0] = new Pawn(this.p1, "A" + (i - 2));
            p2Piece.add((ConcretePiece) this.Game[i][0]);
            AllPiece.add((ConcretePiece) this.Game[i][0]);
            ((ConcretePiece) this.Game[i][0]).addMoves(i, 0);  // Apply change directly to the piece


            this.Game[i][10] = new Pawn(this.p1, "A" + (i + 17));
            p2Piece.add((ConcretePiece) this.Game[i][10]);
            AllPiece.add((ConcretePiece) this.Game[i][10]);
            ((ConcretePiece) this.Game[i][10]).addMoves(i, 10);  // Apply change directly to the piece


        }

        this.Game[5][1] = new Pawn(this.p1, "A6");
        p2Piece.add((ConcretePiece) this.Game[5][1]);
        AllPiece.add((ConcretePiece) this.Game[5][1]);
        ((ConcretePiece) this.Game[5][1]).addMoves(5, 1);


        this.Game[0][3] = new Pawn(this.p1, "A7");
        p2Piece.add((ConcretePiece) this.Game[0][3]);
        AllPiece.add((ConcretePiece) this.Game[0][3]);
        ((ConcretePiece) this.Game[0][3]).addMoves(0, 3);

        this.Game[10][3] = new Pawn(this.p1, "A8");
        p2Piece.add((ConcretePiece) this.Game[10][3]);
        AllPiece.add((ConcretePiece) this.Game[10][3]);
        ((ConcretePiece) this.Game[10][3]).addMoves(10, 3);

        this.Game[0][4] = new Pawn(this.p1, "A9");
        p2Piece.add((ConcretePiece) this.Game[0][4]);
        AllPiece.add((ConcretePiece) this.Game[0][4]);
        ((ConcretePiece) this.Game[0][4]).addMoves(0, 4);

        this.Game[10][4] = new Pawn(this.p1, "A10");
        p2Piece.add((ConcretePiece) this.Game[10][4]);
        AllPiece.add((ConcretePiece) this.Game[10][4]);
        ((ConcretePiece) this.Game[10][4]).addMoves(10, 4);

        this.Game[0][5] = new Pawn(this.p1, "A11");
        p2Piece.add((ConcretePiece) this.Game[0][5]);
        AllPiece.add((ConcretePiece) this.Game[0][5]);
        ((ConcretePiece) this.Game[0][5]).addMoves(0, 5);

        this.Game[1][5] = new Pawn(this.p1, "A12");
        p2Piece.add((ConcretePiece) this.Game[1][5]);
        AllPiece.add((ConcretePiece) this.Game[1][5]);
        ((ConcretePiece) this.Game[1][5]).addMoves(1, 5);

        this.Game[9][5] = new Pawn(this.p1, "A13");
        p2Piece.add((ConcretePiece) this.Game[9][5]);
        AllPiece.add((ConcretePiece) this.Game[9][5]);
        ((ConcretePiece) this.Game[9][5]).addMoves(9, 5);

        this.Game[10][5] = new Pawn(this.p1, "A14");
        p2Piece.add((ConcretePiece) this.Game[10][5]);
        AllPiece.add((ConcretePiece) this.Game[10][5]);
        ((ConcretePiece) this.Game[10][5]).addMoves(10, 5);

        this.Game[0][6] = new Pawn(this.p1, "A15");
        p2Piece.add((ConcretePiece) this.Game[0][6]);
        AllPiece.add((ConcretePiece) this.Game[0][6]);
        ((ConcretePiece) this.Game[0][6]).addMoves(0, 6);

        this.Game[10][6] = new Pawn(this.p1, "A16");
        p2Piece.add((ConcretePiece) this.Game[10][6]);
        AllPiece.add((ConcretePiece) this.Game[10][6]);
        ((ConcretePiece) this.Game[10][6]).addMoves(10, 6);

        this.Game[0][7] = new Pawn(this.p1, "A17");
        p2Piece.add((ConcretePiece) this.Game[0][7]);
        AllPiece.add((ConcretePiece) this.Game[0][7]);
        ((ConcretePiece) this.Game[0][7]).addMoves(0, 7);

        this.Game[10][7] = new Pawn(this.p1, "A18");
        p2Piece.add((ConcretePiece) this.Game[10][7]);
        AllPiece.add((ConcretePiece) this.Game[10][7]);
        ((ConcretePiece) this.Game[10][7]).addMoves(10, 7);

        this.Game[5][9] = new Pawn(this.p1, "A19");
        p2Piece.add((ConcretePiece) this.Game[5][9]);
        AllPiece.add((ConcretePiece) this.Game[5][9]);
        ((ConcretePiece) this.Game[5][9]).addMoves(5, 9);

        this.Game[5][3] = new Pawn(this.p2, "D1");
        p1Piece.add((ConcretePiece) this.Game[5][3]);
        AllPiece.add((ConcretePiece) this.Game[5][3]);
        ((ConcretePiece) this.Game[5][3]).addMoves(5, 3);

        this.Game[4][4] = new Pawn(this.p2, "D2");
        p1Piece.add((ConcretePiece) this.Game[4][4]);
        AllPiece.add((ConcretePiece) this.Game[4][4]);
        ((ConcretePiece) this.Game[4][4]).addMoves(4, 4);

        this.Game[5][4] = new Pawn(this.p2, "D3");
        p1Piece.add((ConcretePiece) this.Game[5][4]);
        AllPiece.add((ConcretePiece) this.Game[5][4]);
        ((ConcretePiece) this.Game[5][4]).addMoves(5, 4);

        this.Game[6][4] = new Pawn(this.p2, "D4");
        p1Piece.add((ConcretePiece) this.Game[6][4]);
        AllPiece.add((ConcretePiece) this.Game[6][4]);
        ((ConcretePiece) this.Game[6][4]).addMoves(6, 4);

        this.Game[3][5] = new Pawn(this.p2, "D5");
        p1Piece.add((ConcretePiece) this.Game[3][5]);
        AllPiece.add((ConcretePiece) this.Game[3][5]);
        ((ConcretePiece) this.Game[3][5]).addMoves(3, 5);

        this.Game[4][5] = new Pawn(this.p2, "D6");
        p1Piece.add((ConcretePiece) this.Game[4][5]);
        AllPiece.add((ConcretePiece) this.Game[4][5]);
        ((ConcretePiece) this.Game[4][5]).addMoves(4, 5);

        this.Game[5][5] = new King(this.p2, "K7");
        p1Piece.add((ConcretePiece) this.Game[5][5]);
        AllPiece.add((ConcretePiece) this.Game[5][5]);
        ((ConcretePiece) this.Game[5][5]).addMoves(5, 5);

        this.Game[6][5] = new Pawn(this.p2, "D8");
        p1Piece.add((ConcretePiece) this.Game[6][5]);
        AllPiece.add((ConcretePiece) this.Game[6][5]);
        ((ConcretePiece) this.Game[6][5]).addMoves(6, 5);

        this.Game[7][5] = new Pawn(this.p2, "D9");
        p1Piece.add((ConcretePiece) this.Game[7][5]);
        AllPiece.add((ConcretePiece) this.Game[7][5]);
        ((ConcretePiece) this.Game[7][5]).addMoves(7, 5);

        this.Game[4][6] = new Pawn(this.p2, "D10");
        p1Piece.add((ConcretePiece) this.Game[4][6]);
        AllPiece.add((ConcretePiece) this.Game[4][6]);
        ((ConcretePiece) this.Game[4][6]).addMoves(4, 6);

        this.Game[5][6] = new Pawn(this.p2, "D11");
        p1Piece.add((ConcretePiece) this.Game[5][6]);
        AllPiece.add((ConcretePiece) this.Game[5][6]);
        ((ConcretePiece) this.Game[5][6]).addMoves(5, 6);

        this.Game[6][6] = new Pawn(this.p2, "D12");
        p1Piece.add((ConcretePiece) this.Game[6][6]);
        AllPiece.add((ConcretePiece) this.Game[6][6]);
        ((ConcretePiece) this.Game[6][6]).addMoves(6, 6);

        this.Game[5][7] = new Pawn(this.p2, "D13");
        p1Piece.add((ConcretePiece) this.Game[5][7]);
        AllPiece.add((ConcretePiece) this.Game[5][7]);
        ((ConcretePiece) this.Game[5][7]).addMoves(5, 7);

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (Game[i][j] != null) {
                    positions[i][j].addMarchedsquares((ConcretePiece) Game[i][j]);
                }
            }
        }


        t = false;

    }

    @Override
    public void undoLastMove() {


    }


    @Override
    public int getBoardSize() {
        return size;
    }
}


