/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package put.ai.games.naiveplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

public class NotNaivePlayer extends Player {

    public class Pair<T> {
        private final T first;
        private final T second;

        public Pair(T first, T second){
            this.first = first;
            this.second = second;
        }

        public T getFirst(){
            return this.first;
        }

        public T getSecond(){
            return this.second;
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

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

    public class Evaluator {
        public Long evaluate(Board board, Player.Color color){
            List<put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer>> positions = new put.ai.games.naiveplayer.NotNaivePlayer.Evaluator().getPositions(board, color);
            Long sum = 0L;
            for(put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> p1 : positions){
                for(put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> p2 : positions){
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
            sum += groupNumber(positions) * 20;
            System.out.println("Total points is: " + sum );
            // halving as we sum each distance from p1 to p2 and from p2 to p1
            return sum;
        }

        private Integer distance(put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> a, put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> b){
            return (int) Math.round(Math.sqrt((a.getFirst() - b.getFirst()) * (a.getFirst() - b.getFirst()) +
                    (a.getSecond() - b.getSecond()) * (a.getSecond() - b.getSecond())));
        }

        private Integer groupNumber(List<put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer>> positions){
            Integer biggest = 0;
            Integer[] done = new Integer[positions.size()];
            Integer[] colors = new Integer[positions.size()];
            Arrays.fill(done, 0);
            Arrays.fill(colors, -1);
            put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> current = positions.get(0);
            put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> next = null;
            Integer color = 0;
            Integer doned = 0; // (xd)
            //System.out.println(positions.toString());
            while(true){
                //System.out.println(color);
                //System.out.println(current.toString());
                for(put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> p1 : positions){
                        if(!p1.equals(current)) {
                            Integer dist = distance(p1, current);
                            if (dist < 2){
                                colors[positions.indexOf(p1)] = color;
                                //System.out.println(p1.toString());
                            }
                        }
                }
                //System.out.println("---------");
                done[positions.indexOf(current)] = 1;
                colors[positions.indexOf(current)] = color;
                doned += 1;
                if(doned == positions.size()) break;
                next = null;
                for(put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer> p1 : positions){
                    if(done[positions.indexOf(p1)] != 1 && colors[positions.indexOf(p1)].equals(color)){
                        next = p1;
                        break;
                    }
                }
                if(next == null){
                    color += 1;
                    int i = 0;
                    while(done[i] == 1) i+= 1;
                    next = positions.get(i);
                }
                current = next;
            }
            //System.out.println(Arrays.toString(colors));
            //System.out.println(Arrays.toString(done));
            Integer[] sums = new Integer[positions.size()];
            Arrays.fill(sums, 0);
            for (Integer integer : colors) sums[integer]++;
            Integer number = 0;
            for(int i = 0; i < colors.length; i++) if(sums[i] != 0) number++;
            // System.out.println(number);
            return number;
        }


        public List<put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer>> getPositions(Board board, Player.Color color){
            List<put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer>> positions = new ArrayList<put.ai.games.naiveplayer.NotNaivePlayer.Pair<Integer>>();
            int size = board.getSize();
            for(int row = 0; row < size; row++){
                for(int col = 0; col < size; col++){
                    if(board.getState(col, row).equals(color))
                        positions.add(new put.ai.games.naiveplayer.NotNaivePlayer.Pair<>(col, row));
                }
            }
            // System.out.println("Player's " + color.toString() + " sheeps are on " + positions.toString());
            return positions;
        }
    }

    public class AlphaBeta {
        public Move getMove(Board board, Player.Color color){
            List<Move> moves = board.getMovesFor(color);
            Long minimal = Long.MAX_VALUE;
            Move bestMove = null;
            for(Move move : moves){
                Board newBoard = board.clone();
                newBoard.doMove(move);
                Long score = miniMax(newBoard, 3, Long.MIN_VALUE, Long.MAX_VALUE, false, color);
                if(score < minimal){
                    bestMove = move;
                    minimal = score;
                }
            }
            return bestMove;
        }

        private Long miniMax(Board board, Integer depth, Long alpha, Long beta, Boolean isMaximizing, Player.Color color){
            if(depth == 0)
                return new put.ai.games.naiveplayer.NotNaivePlayer.Evaluator().evaluate(board, color);
            put.ai.games.naiveplayer.NotNaivePlayer.MoveGenerator moveGenerator = new put.ai.games.naiveplayer.NotNaivePlayer.MoveGenerator(board, color);
            if(isMaximizing && color.equals(Player.Color.PLAYER1))
                moveGenerator = new put.ai.games.naiveplayer.NotNaivePlayer.MoveGenerator(board, Player.Color.PLAYER2);
            else if(isMaximizing && color.equals(Player.Color.PLAYER2))
                moveGenerator = new put.ai.games.naiveplayer.NotNaivePlayer.MoveGenerator(board, Player.Color.PLAYER1);

            if(isMaximizing){
                Move move;
                Long maxScore = Long.MIN_VALUE;
                while((move = moveGenerator.generate()) != null){
                    Board newBoard = board.clone();
                    newBoard.doMove(move);
                    Long score = miniMax(newBoard, depth - 1, alpha, beta, false, color);
                    maxScore = Math.max(score, maxScore);
                    alpha = Math.max(score, alpha);
                    if(beta <= alpha)
                        break;
                }
                return maxScore;
            }
            else{
                Move move;
                Long minScore = Long.MAX_VALUE;
                while((move = moveGenerator.generate()) != null){
                    Board newBoard = board.clone();
                    newBoard.doMove(move);
                    Long score = miniMax(newBoard, depth - 1, alpha, beta, true, color);
                    minScore = Math.min(score, minScore);
                    beta = Math.min(beta, score);
                    if(beta < alpha)
                        break;
                }
                return minScore;
            }
        }
    }

    // private Random random = new Random(0xdeadbeef);


    @Override
    public String getName() {
        return "Piotr Tylczynski 141331 Justyna Grzeszczask 136244";
    }


    @Override
    public Move nextMove(Board b) {
//        List<Move> moves = b.getMovesFor(getColor());
//        System.out.println();
//        Evaluator.evaluate(b, getColor());
//        moves.forEach(a -> System.out.println(a + a.getClass().toString()));
        return new put.ai.games.naiveplayer.NotNaivePlayer.AlphaBeta().getMove(b, getColor());
    }
}
