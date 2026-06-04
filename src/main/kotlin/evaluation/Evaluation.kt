package evaluation

import board.Board
import board.BoardConstants

/**
 * Odpowiada za statyczną ocenę pozycji na planszy (Heurystykę).
 */
object Evaluation {

    // Wartości figur w tzw. centypionach (100 = 1 pion)
    // Indeksy odpowiadają stałym z BoardConstants (0..5)
    private val pieceValues = intArrayOf(
        100,   // PION
        300,   // SKOCZEK
        300,   // GONIEC
        500,   // WIEŻA
        900,   // HETMAN
        10000  // KRÓL (Sztucznie wysoka wartość, aby silnik chronił króla za wszelką cenę)
    )

    /**
     * Oblicza prosty bilans materiału na planszy.
     * Wynik > 0 oznacza przewagę białych, wynik < 0 przewagę czarnych.
     */
    fun evaluate(board: Board): Int {
        var score = 0

        for(i in 0..5){
            val whitePieces = board.pieces[i] and board.colors[BoardConstants.COLOR_WHITE]
            val blackPieces = board.pieces[i] and board.colors[BoardConstants.COLOR_BLACK]
            
            score += whitePieces.countOneBits() * pieceValues[i]
            score -= blackPieces.countOneBits() * pieceValues[i]
        }
        return score
    }
}