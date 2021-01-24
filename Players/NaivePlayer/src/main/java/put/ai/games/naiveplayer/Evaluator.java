package put.ai.games.naiveplayer;

import put.ai.games.game.Board;
import put.ai.games.game.Player;

import java.util.ArrayList;
import java.util.List;

public class Evaluator {
    public static Long evaluate(Board board, Player.Color color){
        List<Pair<Integer>> positions = Evaluator.getPositions(board, color);
        Long sum = 0L;
        for(Pair<Integer> p1 : positions){
            for(Pair<Integer> p2 : positions){
                if(!p1.equals(p2)) {
                    Integer dist = distance(p1, p2);
                    if (dist >= 2) sum += dist;
                    if (dist < 2) sum -= 2;
                }
            }
        }
        sum /= 2;
        Integer opponentMoves = 0;
        Integer remainingSheeps = 0;
        if(color.equals(Player.Color.PLAYER1)) {
            opponentMoves = board.getMovesFor(Player.Color.PLAYER2).size();
            remainingSheeps = getPositions(board, Player.Color.PLAYER2).size();
        }
        else {
            opponentMoves = board.getMovesFor(Player.Color.PLAYER1).size();
            remainingSheeps = getPositions(board, Player.Color.PLAYER1).size();
        }
        sum += opponentMoves * 2;
        sum += ((board.getSize() - 2) * 2 - remainingSheeps) * 15;
        System.out.println("Total distance is: " + sum );
        // halving as we sum each distance from p1 to p2 and from p2 to p1
        return sum;
    }

    private static Integer distance(Pair<Integer> a, Pair<Integer> b){
        return (int) Math.round(Math.sqrt((a.getFirst() - b.getFirst()) * (a.getFirst() - b.getFirst()) +
                (a.getSecond() - b.getSecond()) * (a.getSecond() - b.getSecond())));
    }

    static List<Pair<Integer>> getPositions(Board board, Player.Color color){
        List<Pair<Integer>> positions = new ArrayList<Pair<Integer>>();
        int size = board.getSize();
        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                if(board.getState(col, row).equals(color))
                    positions.add(new Pair<>(col, row));
            }
        }
        // System.out.println("Player's " + color.toString() + " sheeps are on " + positions.toString());
        return positions;
    }
}
