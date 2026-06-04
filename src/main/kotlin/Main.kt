import board.Board
import board.BoardConstants
import board.Move
import movegen.Perft
import uci.UciLoop

fun main() {
    UciLoop.loop()
}

/**
 * Funkcja pomocnicza do ręcznego ustawienia pozycji startowej 
 * przed zaimplementowaniem parsera FEN.
 */
fun setupStandardPosition(board: Board) {
    // Białe figury
    board.setPiece(BoardConstants.SQUARE_A1, BoardConstants.PIECE_ROOK, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_B1, BoardConstants.PIECE_KNIGHT, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_C1, BoardConstants.PIECE_BISHOP, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_D1, BoardConstants.PIECE_QUEEN, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_E1, BoardConstants.PIECE_KING, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_F1, BoardConstants.PIECE_BISHOP, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_G1, BoardConstants.PIECE_KNIGHT, BoardConstants.COLOR_WHITE)
    board.setPiece(BoardConstants.SQUARE_H1, BoardConstants.PIECE_ROOK, BoardConstants.COLOR_WHITE)
    for (i in BoardConstants.SQUARE_A2..BoardConstants.SQUARE_H2) {
        board.setPiece(i, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_WHITE)
    }

    // Czarne figury
    board.setPiece(BoardConstants.SQUARE_A8, BoardConstants.PIECE_ROOK, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_B8, BoardConstants.PIECE_KNIGHT, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_C8, BoardConstants.PIECE_BISHOP, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_D8, BoardConstants.PIECE_QUEEN, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_E8, BoardConstants.PIECE_KING, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_F8, BoardConstants.PIECE_BISHOP, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_G8, BoardConstants.PIECE_KNIGHT, BoardConstants.COLOR_BLACK)
    board.setPiece(BoardConstants.SQUARE_H8, BoardConstants.PIECE_ROOK, BoardConstants.COLOR_BLACK)
    for (i in BoardConstants.SQUARE_A7..BoardConstants.SQUARE_H7) {
        board.setPiece(i, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_BLACK)
    }

    // Ustawienie początkowego stanu gry
    board.sideToMove = BoardConstants.COLOR_WHITE
    board.stateHistory[board.currentHalfMove].castlingRights = 15 // 1111 binarnie (wszystkie roszady dostępne)
    board.stateHistory[board.currentHalfMove].enPassantSquare = -1
}