package movegen

import board.Board
import board.BoardConstants
import board.Move

/**
 * Narzędzie do testowania wydajności i poprawności generatora ruchów (Perft - Performance Test).
 * Przeszukuje drzewo gry rekurencyjnie do zadanej głębokości i zlicza wszystkie w 100% legalne węzły-liście.
 */
object Perft {

    /**
     * Główna funkcja rekurencyjna.
     * 
     * @param board Aktualny stan szachownicy.
     * @param depth Głębokość, na którą chcemy jeszcze zejść.
     * @return Liczba unikalnych, legalnych pozycji z węzłów liści (na samym dole).
     */
    private fun perftDriver(board: Board, depth: Int): Long {
        if (depth == 0) return 1L

        var nodes = 0L
        val color = board.sideToMove
        val moveList = MoveGenerator.generateMoves(board, color)
        
        for (i in 0 until moveList.count) {
            val move = moveList.moves[i]
            
            board.makeMove(move)
            
            val kingSquare = board.pieces[BoardConstants.PIECE_KING] and board.colors[color]
            val kingSquareIndex = kingSquare.countTrailingZeroBits()
            if (!board.isSquareAttacked(kingSquareIndex, board.sideToMove)) {
                nodes += perftDriver(board, depth - 1)
            }
            
            board.unmakeMove(move)
        }

        return nodes
    }

    /**
     * Uruchamia test PERFT dla podanej szachownicy i głębokości, mierząc czas i NPS.
     * Wypisuje w konsoli liczbę wariantów dla każdego ruchu startowego (w formacie UCI).
     * 
     * @param board Aktualny stan szachownicy, od którego zaczynamy test.
     * @param depth Głębokość testu.
     */
    fun perftTest(board: Board, depth: Int) {
        println("Rozpoczynam test PERFT dla głębokości: $depth")
        
        var totalNodes = 0L
        val startTime = System.currentTimeMillis()

        val color = board.sideToMove
        val moveList = MoveGenerator.generateMoves(board, color)

        for (i in 0 until moveList.count) {
            val move = moveList.moves[i]
            board.makeMove(move)

            val kingSquare = board.pieces[BoardConstants.PIECE_KING] and board.colors[color]
            val kingSquareIndex = kingSquare.countTrailingZeroBits()
            if (!board.isSquareAttacked(kingSquareIndex, board.sideToMove)) {
                val nodes = perftDriver(board, depth - 1)
                println("${moveToUciString(move)}: $nodes")
                totalNodes += nodes
            }

            board.unmakeMove(move)
        }

        val endTime = System.currentTimeMillis()
        val timeTaken = endTime - startTime
        val nodesPerSecond = (totalNodes * 1000) / timeTaken.coerceAtLeast(1)

        println("\n--- WYNIKI PERFT ---")
        println("Calkowita liczba wezlow: $totalNodes")
        println("Czas wykonania: $timeTaken ms")
        println("NPS: $nodesPerSecond")
    }

    /**
     * Konwertuje skompresowany 32-bitowy ruch na format tekstowy UCI (np. "e2e4", "e7e8q").
     * Protokół UCI wymaga podania wyłącznie pola startowego, docelowego i ewentualnej promocji.
     *
     * @param move Skompresowany ruch w postaci liczby [Int].
     * @return Reprezentacja tekstowa ruchu zgodna ze standardem UCI.
     */
    private fun moveToUciString(move: Int): String {
        val source = Move.getSourceSquare(move)
        val target = Move.getTargetSquare(move)
        val promotedPiece = Move.getPromotedPiece(move)

        val sourceColumn = 'a' + (source % 8)
        val sourceRow = (source / 8) + 1
        val targetColumn = 'a' + (target % 8)
        val targetRow = (target / 8) + 1

        var uci = "$sourceColumn$sourceRow$targetColumn$targetRow"

        if (promotedPiece != 0) {
            uci += when (promotedPiece) {
                BoardConstants.PIECE_KNIGHT -> "n"
                BoardConstants.PIECE_BISHOP -> "b"
                BoardConstants.PIECE_ROOK -> "r"
                BoardConstants.PIECE_QUEEN -> "q"
                else -> ""
            }
        }
        return uci
    }
}