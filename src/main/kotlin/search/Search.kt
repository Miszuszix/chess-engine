package search

import board.Board
import board.BoardConstants
import board.Move
import board.Move.moveToUciString
import evaluation.Evaluation
import movegen.MoveGenerator
import movegen.Perft.perftDriver
import kotlin.countTrailingZeroBits

/**
 * Mózg silnika. Odpowiada za przeszukiwanie drzewa gry i znajdowanie najlepszego ruchu.
 */
object Search {

    const val INFINITY = 50000
    const val MATE_VALUE = 49000

    /**
     * Główna funkcja rozpoczynająca przeszukiwanie (tzw. Root).
     * Szuka najlepszego ruchu dla aktualnego gracza na podaną głębokość.
     * Podczas przeszukiwania, wysyła na standardowe wyjście informacje w formacie UCI (`info ...`).
     *
     * @param board Szachownica w aktualnej pozycji.
     * @param depth Głębokość, na jaką chcemy przeszukać drzewo gry.
     * @return 32-bitowy kod najlepszego ruchu.
     */

    var nodes = 0L

    fun searchPosition(board: Board, depth: Int): Int {
        val startTime = System.currentTimeMillis()
        nodes = 0L

        var bestMove = 0
        var alpha = -INFINITY
        val beta = INFINITY

        val color = board.sideToMove
        val moveList = MoveGenerator.generateMoves(board, color)

        for (i in 0 until moveList.count) {
            val move = moveList.moves[i]
            board.makeMove(move)

            if (isKingAttacked(board, color)){
                board.unmakeMove(move)
                continue
            }

            val score = -negamax(board, depth - 1, -beta, -alpha)

            board.unmakeMove(move)

            if (score > alpha) {
                alpha = score
                bestMove = move
            }
        }

        val timeMs = System.currentTimeMillis() - startTime
        val nps = (nodes / (timeMs.coerceAtLeast(1) / 1000.0)).toLong()

        val stats = "info depth $depth score cp $alpha nodes $nodes nps $nps time $timeMs pv " + Move.moveToUciString(bestMove)
        println(stats)

        return bestMove
    }

    /**
     * Rekurencyjna funkcja Negamax z odcięciami Alpha-Beta.
     *
     * @param board Aktualny stan szachownicy.
     * @param depth Pozostała głębokość do przeszukania.
     * @param alpha Dolna granica okna przeszukiwania (najlepszy pewny wynik dla nas).
     * @param beta Górna granica okna przeszukiwania (maksimum na co pozwoli nam przeciwnik).
     * @return Ocena pozycji w centypionach.
     */
    private fun negamax(board: Board, depth: Int, alpha: Int, beta: Int): Int {
        nodes++
        if (depth == 0) {
            val evaluation = Evaluation.evaluate(board)
            val scoreMultiplier = if (board.sideToMove == BoardConstants.COLOR_WHITE) 1 else -1
            return evaluation * scoreMultiplier
        }

        var currentAlpha = alpha
        var legalMovesCount = 0
        val color = board.sideToMove
        val moveList = MoveGenerator.generateMoves(board, color)

        for (i in 0 until moveList.count) {
            val move = moveList.moves[i]
            board.makeMove(move)

            if (isKingAttacked(board, color)){
                board.unmakeMove(move)
                continue
            }

            legalMovesCount++

            val score = -negamax(board, depth - 1, -beta, -currentAlpha)

            board.unmakeMove(move)

            if(score >= beta) return beta
            
            if (score > currentAlpha) currentAlpha = score
        }

        if (legalMovesCount == 0){
            val kingSquare = board.pieces[BoardConstants.PIECE_KING] and board.colors[color]
            val kingSquareIndex = kingSquare.countTrailingZeroBits()
            if (board.isSquareAttacked(kingSquareIndex, color xor 1)) {
                return -MATE_VALUE
            }
            return 0
        }

        return currentAlpha
    }

    /**
     * Sprawdza, czy król gracza, który właśnie wykonał ruch, jest narażony na atak (co czyniłoby ruch nielegalnym).
     */
    private fun isKingAttacked(board: Board, color: Int): Boolean{
        val kingSquare = board.pieces[BoardConstants.PIECE_KING] and board.colors[color]
        val kingSquareIndex = kingSquare.countTrailingZeroBits()
        return board.isSquareAttacked(kingSquareIndex, color xor 1)
    }
}