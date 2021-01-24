package put.ai.games.naiveplayer;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MoveGenerator {
    private final Random random = new Random();
    private final List<Move> moves;

    public MoveGenerator(Board board, Player.Color color){
        this.moves = board.getMovesFor(color);
    }

    public Move generate(){
        if(moves.size() <= 0) return null;
        else {
            Move move = moves.get(Math.abs(this.random.nextInt()) % moves.size());
            this.moves.remove(move);
            return move;
        }
    }

}
