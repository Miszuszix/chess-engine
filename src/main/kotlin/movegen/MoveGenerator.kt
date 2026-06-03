package movegen

import board.Board
import board.BoardConstants
import board.Move
import kotlin.countTrailingZeroBits
import movegen.BishopAttacks
import movegen.RookAttacks
import movegen.QueenAttacks
import board.Bitboard
import board.BoardConstants.SQUARE_B1
import board.BoardConstants.SQUARE_B8
import board.BoardConstants.SQUARE_C1
import board.BoardConstants.SQUARE_C8
import board.BoardConstants.SQUARE_D1
import board.BoardConstants.SQUARE_D8
import board.BoardConstants.SQUARE_E1
import board.BoardConstants.SQUARE_E8
import board.BoardConstants.SQUARE_F1
import board.BoardConstants.SQUARE_F8
import board.BoardConstants.SQUARE_G1
import board.BoardConstants.SQUARE_G8

/**
 * Główny generator ruchów (Pseudo-Legal).
 * Odpowiada za wygenerowanie wszystkich teoretycznie możliwych posunięć
 * w danej pozycji (bez sprawdzania, czy król po ruchu nie jest w szachu).
 */
object MoveGenerator {

    /**
     * Zoptymalizowana lista ruchów oparta na tablicy typów prostych (Int).
     * Unika narzutu pamięciowego i kosztów alokacji związanych z użyciem standardowego `ArrayList<Int>`.
     */
    class MoveList {
        /** Tablica przechowująca zakodowane ruchy (max 256 ruchów w jednej pozycji). */
        val moves = IntArray(256)
        /** Aktualna liczba wygenerowanych ruchów. */
        var count = 0

        /**
         * Dodaje nowy zakodowany ruch do listy.
         * @param move 32-bitowa liczba całkowita reprezentująca ruch.
         */
        fun add(move: Int) {
            moves[count] = move
            count++
        }
    }

    /**
     * Główna funkcja generująca ruchy dla danego koloru w danej pozycji.
     * 
     * @param board Aktualny stan szachownicy.
     * @param color Kolor, dla którego generujemy ruchy ([BoardConstants.COLOR_WHITE] lub [BoardConstants.COLOR_BLACK]).
     * @return Zapełniony obiekt [MoveList] zawierający wszystkie pseudo-legalne ruchy.
     */
    fun generateMoves(board: Board, color: Int): MoveList {
        val moveList = MoveList()

        val ourPieces = board.colors[color]
        val enemyPieces = board.colors[color xor 1]
        val occupancy = ourPieces or enemyPieces

        generatePieceMoves(BoardConstants.PIECE_KNIGHT, board.pieces[BoardConstants.PIECE_KNIGHT], ourPieces, enemyPieces, moveList) { square ->
            KnightAttacks.attacks[square]
        }

        generatePieceMoves(BoardConstants.PIECE_KING, board.pieces[BoardConstants.PIECE_KING], ourPieces, enemyPieces, moveList) { square ->
            KingAttacks.attacks[square]
        }

        generatePieceMoves(BoardConstants.PIECE_BISHOP, board.pieces[BoardConstants.PIECE_BISHOP], ourPieces, enemyPieces, moveList) { square ->
            BishopAttacks.getAttacks(square, occupancy)
        }

        generatePieceMoves(BoardConstants.PIECE_ROOK, board.pieces[BoardConstants.PIECE_ROOK], ourPieces, enemyPieces, moveList) { square ->
            RookAttacks.getAttacks(square, occupancy)
        }

        generatePieceMoves(BoardConstants.PIECE_QUEEN, board.pieces[BoardConstants.PIECE_QUEEN], ourPieces, enemyPieces, moveList) { square ->
            QueenAttacks.getAttacks(square, occupancy)
        }

        generatePawnMoves(board, color, enemyPieces, occupancy, moveList)

        generateCastlingMoves(board, color, occupancy, moveList)

        return moveList
    }

    /**
     * Uniwersalna, wbudowana (inline) funkcja do generowania ruchów dla figur skokowych i liniowych.
     * Użycie słowa `inline` zapobiega alokacji obiektu zamknięcia (closure) dla lambdy [getAttacks].
     *
     * @param pieceType Typ figury, dla której generujemy ruchy.
     * @param pieceBitboard Bitboard przechowujący pozycje wszystkich figur danego typu.
     * @param ourPieces Bitboard ze wszystkimi figurami gracza, który aktualnie się porusza.
     * @param enemyPieces Bitboard ze wszystkimi figurami przeciwnika.
     * @param moveList Lista, do której dodawane są wygenerowane ruchy.
     * @param getAttacks Funkcja dostarczająca maskę ataków dla danego pola.
     */
    private inline fun generatePieceMoves(
        pieceType: Int,
        pieceBitboard: ULong,
        ourPieces: ULong,
        enemyPieces: ULong,
        moveList: MoveList,
        getAttacks: (Int) -> ULong
    ) {
        var pieces = pieceBitboard and ourPieces

        while (pieces != 0UL) {
            val sourceSquare = pieces.countTrailingZeroBits()

            var validMoves = getAttacks(sourceSquare) and ourPieces.inv()

            while (validMoves != 0UL) {
                val targetSquare = validMoves.countTrailingZeroBits()

                val isCapture: Boolean = enemyPieces shr targetSquare and 1UL == 1UL
                val move = Move.encode(sourceSquare, targetSquare, pieceType, isCapture = isCapture)

                moveList.add(move)
                validMoves = validMoves and (validMoves - 1UL)
            }
            pieces = pieces and (pieces - 1UL)
        }
    }

    /**
     * Generuje specjalne ruchy pionów (pchnięcia, bicia, promocje, en passant).
     *
     * @param board Stan szachownicy.
     * @param color Kolor pionów.
     * @param enemyPieces Bitboard z figurami przeciwnika.
     * @param occupancy Bitboard ze wszystkimi zajętymi polami.
     * @param moveList Lista, do której dodawane są ruchy.
     */
    private fun generatePawnMoves(
        board: Board,
        color: Int,
        enemyPieces: ULong,
        occupancy: ULong,
        moveList: MoveList
    ) {
        var pawns = board.pieces[BoardConstants.PIECE_PAWN] and board.colors[color]
        
        val direction = if (color == BoardConstants.COLOR_WHITE) 8 else -8
        
        val startingRank = if (color == BoardConstants.COLOR_WHITE) 1 else 6 
        val promotionRank = if (color == BoardConstants.COLOR_WHITE) 7 else 0

        while (pawns != 0UL) {
            val sourceSquare = pawns.countTrailingZeroBits()
            val row = sourceSquare / 8
            
            val singlePushSquare = sourceSquare + direction
            
            if (!Bitboard.getBit(occupancy, singlePushSquare)){
                if (singlePushSquare / 8 == promotionRank){
                    addPromotionMoves(sourceSquare, singlePushSquare, false, moveList)
                }else{
                    moveList.add(Move.encode(sourceSquare, singlePushSquare, BoardConstants.PIECE_PAWN))
                }

                if(row == startingRank){
                    val doublePushSquare = sourceSquare + (direction * 2)
                    if(!Bitboard.getBit(occupancy, doublePushSquare)){
                        moveList.add(Move.encode(sourceSquare, doublePushSquare, BoardConstants.PIECE_PAWN, isDoublePawnPush = true))
                    }
                }
            }

            var attacks = PawnAttacks.attacks[color][sourceSquare] and enemyPieces
            
            while (attacks != 0UL) {
                val targetSquare = attacks.countTrailingZeroBits()
                
                if(targetSquare / 8 == promotionRank){
                    addPromotionMoves(sourceSquare, targetSquare, true, moveList)
                }else{
                    moveList.add(Move.encode(sourceSquare, targetSquare, BoardConstants.PIECE_PAWN, isCapture = true))
                }
                
                attacks = attacks and (attacks - 1UL) 
            }
            
            val epSquare = board.stateHistory[board.currentHalfMove].enPassantSquare
            if(epSquare != -1){
                val attacks = PawnAttacks.attacks[color][sourceSquare]
                if(Bitboard.getBit(attacks, epSquare)){
                    moveList.add(Move.encode(sourceSquare, epSquare, BoardConstants.PIECE_PAWN, isCapture = true, isEnPassant = true))
                }
            }
            pawns = pawns and (pawns - 1UL)
        }
    }

    /**
     * Dodaje cztery ruchy promocyjne do listy (Hetman, Wieża, Goniec, Skoczek).
     *
     * @param sourceSquare Pole startowe piona.
     * @param targetSquare Pole docelowe (ostatnia linia).
     * @param isCapture Czy promocja połączona jest z biciem.
     * @param moveList Lista, do której dodawane są ruchy.
     */
    private fun addPromotionMoves(sourceSquare: Int, targetSquare: Int, isCapture: Boolean, moveList: MoveList) {
        moveList.add(Move.encode(sourceSquare, targetSquare, BoardConstants.PIECE_PAWN, promotedPiece = BoardConstants.PIECE_QUEEN, isCapture = isCapture))
        moveList.add(Move.encode(sourceSquare, targetSquare, BoardConstants.PIECE_PAWN, promotedPiece = BoardConstants.PIECE_ROOK, isCapture = isCapture))
        moveList.add(Move.encode(sourceSquare, targetSquare, BoardConstants.PIECE_PAWN, promotedPiece = BoardConstants.PIECE_BISHOP, isCapture = isCapture))
        moveList.add(Move.encode(sourceSquare, targetSquare, BoardConstants.PIECE_PAWN, promotedPiece = BoardConstants.PIECE_KNIGHT, isCapture = isCapture))
    }

    /**
     * Generuje możliwe roszady dla króla.
     *
     * @param board Stan szachownicy (do sprawdzania ataków).
     * @param color Kolor gracza.
     * @param occupancy Bitboard ze wszystkimi zajętymi polami.
     * @param moveList Lista, do której dodawane są ruchy.
     */
    private fun generateCastlingMoves(board: Board, color: Int, occupancy: ULong, moveList: MoveList) {
        val castlingRights = board.stateHistory[board.currentHalfMove].castlingRights

        if (color == BoardConstants.COLOR_WHITE) {
            if(board.isSquareAttacked(SQUARE_E1, BoardConstants.COLOR_BLACK)) return

            if(
                castlingRights and 1 != 0 &&
                !Bitboard.getBit(occupancy, SQUARE_F1) &&
                !Bitboard.getBit(occupancy, SQUARE_G1) &&
                !board.isSquareAttacked(SQUARE_F1, BoardConstants.COLOR_BLACK) &&
                !board.isSquareAttacked(SQUARE_G1, BoardConstants.COLOR_BLACK)
                ){
                moveList.add(Move.encode(SQUARE_E1, SQUARE_G1, BoardConstants.PIECE_KING, isCastling = true))
            }

            if(
                castlingRights and 2 != 0 &&
                !Bitboard.getBit(occupancy, SQUARE_D1) &&
                !Bitboard.getBit(occupancy, SQUARE_C1) &&
                !Bitboard.getBit(occupancy, SQUARE_B1) &&
                !board.isSquareAttacked(SQUARE_D1, BoardConstants.COLOR_BLACK) &&
                !board.isSquareAttacked(SQUARE_C1, BoardConstants.COLOR_BLACK)
            ){
                moveList.add(Move.encode(SQUARE_E1, SQUARE_C1, BoardConstants.PIECE_KING, isCastling = true))
            }

        } else {
            if(board.isSquareAttacked(SQUARE_E8, BoardConstants.COLOR_WHITE)) return

            if(
                castlingRights and 4 != 0 &&
                !Bitboard.getBit(occupancy, SQUARE_F8) &&
                !Bitboard.getBit(occupancy, SQUARE_G8) &&
                !board.isSquareAttacked(SQUARE_F8, BoardConstants.COLOR_WHITE) &&
                !board.isSquareAttacked(SQUARE_G8, BoardConstants.COLOR_WHITE)
            ){
                moveList.add(Move.encode(SQUARE_E8, SQUARE_G8, BoardConstants.PIECE_KING, isCastling = true))
            }

            if(
                castlingRights and 8 != 0 &&
                !Bitboard.getBit(occupancy, SQUARE_D8) &&
                !Bitboard.getBit(occupancy, SQUARE_C8) &&
                !Bitboard.getBit(occupancy, SQUARE_B8) &&
                !board.isSquareAttacked(SQUARE_D8, BoardConstants.COLOR_WHITE) &&
                !board.isSquareAttacked(SQUARE_C8, BoardConstants.COLOR_WHITE)
            ){
                moveList.add(Move.encode(SQUARE_E8, SQUARE_C8, BoardConstants.PIECE_KING, isCastling = true))
            }
        }
    }
}