package uci

import board.Board
import java.util.Scanner
import movegen.MoveGenerator
import board.Move
import search.Search

/**
 * Główna pętla komunikacyjna silnika.
 * Odbiera komendy ze standardowego wejścia (GUI) i odpowiada w standardzie UCI.
 */
object UciLoop {

    fun loop() {
        val board = Board()
        val scanner = Scanner(System.`in`)

        // Pętla nieskończona - nasłuchujemy komend dopóki GUI nas nie wyłączy
        while (true) {
            if (!scanner.hasNextLine()) continue
            val line = scanner.nextLine().trim()
            if (line.isEmpty()) continue

            // Rozbijamy komendę po spacjach, by łatwiej ją przetworzyć
            val tokens = line.split(" ")
            val command = tokens[0]

            when (command) {
                "uci" -> {
                    println("id name Mrufka Chess Engine")
                    println("id author Miszuszix")
                    println("uciok")
                }
                "isready" -> {
                    println("readyok")
                }
                "position" -> {
                    parsePosition(board, line)
                }
                "go" -> {
                    val bestMove = Search.searchPosition(board, 5)
                    println("bestmove ${Move.moveToUciString(bestMove)}")
                }
                "quit" -> {
                    return
                }
            }
        }
    }

    /**
     * Analizuje komendę `position` i ustawia odpowiedni stan na planszy,
     * włączając w to zaaplikowanie wszystkich podanych ruchów z historii.
     */
    private fun parsePosition(board: Board, command: String) {
        var movesIndex = command.indexOf("moves")
        
        // 1. Wczytanie pozycji (startpos lub FEN)
        if (command.contains("startpos")) {
            board.loadFromFen("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1")
        } else if (command.contains("fen")) {
            val fenStartIndex = command.indexOf("fen") + 4
            val fenString = if (movesIndex != -1) {
                command.substring(fenStartIndex, movesIndex - 1)
            } else {
                command.substring(fenStartIndex)
            }
            board.loadFromFen(fenString)
        }
        
        // 2. Aplikowanie ruchów (jeśli istnieją)
        if (movesIndex != -1) {
            val movesString = command.substring(movesIndex + 6)
            val moveTokens = movesString.split(" ")
            for (moveStr in moveTokens) {
                val move = parseMove(board, moveStr)
                if (move != 0) {
                    board.makeMove(move)
                }
            }
        }
    }

    /**
     * Parsuje tekstowy ruch w formacie UCI i zamienia go na wewnętrzny, 32-bitowy kod ruchu.
     * Wykorzystuje generator ruchów do znalezienia dopasowania, gwarantując, że
     * zwracany ruch jest poprawny w kontekście obecnego stanu planszy.
     *
     * @param board Aktualny stan szachownicy.
     * @param moveString Ruch w formacie UCI (np. "e2e4", "e7e8q").
     * @return Skompresowany, 32-bitowy ruch [Int], lub 0 jeśli ruch jest nieprawidłowy/nie znaleziono.
     */
    private fun parseMove(board: Board, moveString: String): Int {
        val moves = MoveGenerator.generateMoves(board,board.sideToMove)
        for(i in 0 until moves.count){
            if (Move.moveToUciString(moves.moves[i]) == moveString){
                return moves.moves[i]
            }
        }
        return 0
    }
}