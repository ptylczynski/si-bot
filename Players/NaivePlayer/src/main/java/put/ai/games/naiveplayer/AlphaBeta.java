package put.ai.games.naiveplayer;

import put.ai.games.game.Board;
import put.ai.games.game.Move;
import put.ai.games.game.Player;

import java.util.List;

public class AlphaBeta {
    public static Move getMove(Board board, Player.Color color){
        List<Move> moves = board.getMovesFor(color);
        Long minimal = Long.MAX_VALUE;
        Move bestMove = null;
        for(Move move : moves){
            Board newBoard = board.clone();
            newBoard.doMove(move);
            Long score = AlphaBeta.miniMax(newBoard, 3, Long.MIN_VALUE, Long.MAX_VALUE, false, color);
            if(score < minimal){
                bestMove = move;
                minimal = score;
            }
        }
        return bestMove;
    }

    static Long miniMax(Board board, Integer depth, Long alpha, Long beta, Boolean isMaximizing, Player.Color color){
        if(depth == 0)
            return Evaluator.evaluate(board, color);
        MoveGenerator moveGenerator = new MoveGenerator(board, color);
        if(isMaximizing && color.equals(Player.Color.PLAYER1))
             moveGenerator = new MoveGenerator(board, Player.Color.PLAYER2);
        else if(isMaximizing && color.equals(Player.Color.PLAYER2))
            moveGenerator = new MoveGenerator(board, Player.Color.PLAYER1);

        if(isMaximizing){
            Move move;
            Long maxScore = Long.MIN_VALUE;
            while((move = moveGenerator.generate()) != null){
                Board newBoard = board.clone();
                newBoard.doMove(move);
                Long score = AlphaBeta.miniMax(newBoard, depth - 1, alpha, beta, false, color);
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
