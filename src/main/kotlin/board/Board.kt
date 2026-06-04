package board

import board.BoardConstants.PIECE_BISHOP
import board.BoardConstants.PIECE_KNIGHT
import board.BoardConstants.PIECE_PAWN
import board.BoardConstants.PIECE_ROOK
import board.BoardConstants.PIECE_QUEEN
import board.BoardConstants.PIECE_KING
import movegen.PawnAttacks
import movegen.KnightAttacks
import movegen.KingAttacks
import movegen.BishopAttacks
import movegen.RookAttacks

/**
 * Główna klasa reprezentująca stan szachownicy.
 * Wykorzystuje architekturę Bitboardów dla maksymalnej wydajności.
 * Zamiast alokować nowe obiekty przy każdym ruchu, przechowuje historię
 * w pre-alokowanej tablicy [stateHistory] obsługiwanej indeksem [ply].
 */
class Board {

    /** Tablice przechowujące bitboardy dla każdego typu figury (0 = Pion, ..., 5 = Król). */
    val pieces = ULongArray(6)

    /** Tablice przechowujące bitboardy dla każdego koloru (0 = Białe, 1 = Czarne). */
    val colors = ULongArray(2)

    /** 
     * Pre-alokowana tablica historii stanu gry. 
     * Służy do zapamiętywania nieodwracalnych zmian (prawa do roszady, en passant, zasada 50 ruchów) 
     * w celu wycofywania ruchów bez alokacji pamięci (Unmake Move).
     */
    val stateHistory = Array(1024) { StateInfo() }

    /** Aktualny numer półruchu od początku partii. Służy jako wskaźnik głębokości i indeks do [stateHistory]. */
    var currentHalfMove = 0

    /** Flaga określająca kolor gracza, który aktualnie ma posunięcie (0 = Białe, 1 = Czarne). */
    var sideToMove = BoardConstants.COLOR_WHITE

    /**
     * Umieszcza figurę na szachownicy.
     * Aktualizuje jednocześnie bitboard odpowiedniego typu figury oraz jej koloru.
     *
     * @param square Indeks docelowego pola (0..63).
     * @param piece  Typ figury (np. [BoardConstants.PIECE_PAWN]).
     * @param color  Kolor figury ([BoardConstants.COLOR_WHITE] lub [BoardConstants.COLOR_BLACK]).
     */
    fun setPiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.setBit(pieces[piece], square)
        colors[color] = Bitboard.setBit(colors[color], square)
    }

    /**
     * Zdejmuje figurę z szachownicy.
     * Czyści odpowiedni bit zarówno w bitboardzie typu figury, jak i w bitboardzie koloru.
     *
     * @param square Indeks pola, z którego usuwamy figurę (0..63).
     * @param piece  Typ figury do usunięcia.
     * @param color  Kolor figury do usunięcia.
     */
    fun removePiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.clearBit(pieces[piece], square)
        colors[color] = Bitboard.clearBit(colors[color], square)
    }

    /**
     * Wykonuje podany ruch na planszy modyfikując jej stan (Make Move).
     * Aktualizuje bitboardy, zapisuje nowy stan w historii ([stateHistory]) i przekazuje turę przeciwnikowi.
     *
     * @param move Skompresowany, 32-bitowy kod ruchu zawierający informacje o polu startowym, docelowym i flagach specjalnych.
     */
    fun makeMove(move: Int) {
        val source = Move.getSourceSquare(move)
        val target = Move.getTargetSquare(move)
        val piece = Move.getPiece(move)
        val isCapture = Move.isCapture(move)
        val isDoublePawnPush = Move.isDoublePawnPush(move)
        val isEnPassant = Move.isEnPassant(move)
        val isCastling = Move.isCastling(move)
        val promotedPiece = Move.getPromotedPiece(move)

        val currentState = stateHistory[currentHalfMove]
        val nextState = stateHistory[currentHalfMove + 1]

        // 1. Klonowanie stanu nieodwracalnego
        nextState.castlingRights = currentState.castlingRights
        nextState.halfMoveClock = currentState.halfMoveClock + 1
        nextState.enPassantSquare = -1
        nextState.capturedPiece = -1

        // 2. Resetowanie reguły 50 ruchów
        if (isCapture || piece == BoardConstants.PIECE_PAWN) {
            nextState.halfMoveClock = 0
        }

        // 3. Zwiększenie głębokości (ply)
        currentHalfMove++

        // 4. Obsługa bicia standardowego
        if(isCapture && !isEnPassant){
            for(capturedPiece in 0..5){
                if(Bitboard.getBit(pieces[capturedPiece], target)){
                    nextState.capturedPiece = capturedPiece
                    removePiece(target, capturedPiece, sideToMove xor 1)
                    break
                }
            }
        }

        // 5. Obsługa bicia w przelocie (En Passant)
        if(isCapture && isEnPassant){
            nextState.capturedPiece = BoardConstants.PIECE_PAWN
            if (sideToMove == BoardConstants.COLOR_WHITE) {
                removePiece(target - 8, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_BLACK)
            } else {
                removePiece(target + 8, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_WHITE)
            }
        }

        // 6. Fizyczne przesunięcie figury
        removePiece(source, piece, sideToMove)
        setPiece(target, piece, sideToMove)

        // 7. Obsługa roszady
        if (isCastling){
            when(target){
                BoardConstants.SQUARE_G1 -> {
                    removePiece(BoardConstants.SQUARE_H1, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_F1, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_C1 -> {
                    removePiece(BoardConstants.SQUARE_A1, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_D1, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_G8 -> {
                    removePiece(BoardConstants.SQUARE_H8, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_F8, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_C8 -> {
                    removePiece(BoardConstants.SQUARE_A8, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_D8, BoardConstants.PIECE_ROOK, sideToMove)
                }
            }
        }

        // 8. Ustawienie pola En Passant po podwójnym skoku piona
        if(isDoublePawnPush){
            if(sideToMove == BoardConstants.COLOR_WHITE){
                nextState.enPassantSquare = source + 8
            }else{
                nextState.enPassantSquare = source - 8
            }
        }

        // 9. Obsługa promocji
        if(promotedPiece != 0){
            removePiece(target, PIECE_PAWN, sideToMove)
            setPiece(target, promotedPiece, sideToMove)
        }

        // 10. Aktualizacja praw do roszady za pomocą masek bitowych
        nextState.castlingRights = nextState.castlingRights and (BoardConstants.CASTLING_RIGHTS_UPDATE[source] and BoardConstants.CASTLING_RIGHTS_UPDATE[target])

        // 11. Zmiana tury
        sideToMove = sideToMove xor 1
    }

    /**
     * Cofa podany ruch na planszy (Unmake Move).
     * Odtwarza stan bitboardów oraz cofa się w historii stanu.
     *
     * @param move Skompresowany kod ruchu, który właśnie cofamy.
     */
    fun unmakeMove(move: Int) {
        sideToMove = sideToMove xor 1

        val source = Move.getSourceSquare(move)
        val target = Move.getTargetSquare(move)
        val piece = Move.getPiece(move)
        val isCapture = Move.isCapture(move)
        val isEnPassant = Move.isEnPassant(move)
        val isCastling = Move.isCastling(move)
        val promotedPiece = Move.getPromotedPiece(move)

        val capturedPiece = stateHistory[currentHalfMove].capturedPiece

        currentHalfMove--

        // Cofnięcie fizycznego ruchu (w tym zniwelowanie promocji piona)
        if(promotedPiece != 0){
            removePiece(target, promotedPiece, sideToMove)
            setPiece(source, PIECE_PAWN, sideToMove)
        }else{
            removePiece(target, piece, sideToMove)
            setPiece(source, piece, sideToMove)
        }

        // Przywrócenie zbitej figury przeciwnika
        if(isCapture && !isEnPassant){
            setPiece(target, capturedPiece, sideToMove xor 1)
        }

        // Przywrócenie piona po biciu w przelocie (En Passant)
        if(isCapture && isEnPassant){
            if(sideToMove == BoardConstants.COLOR_WHITE){
                setPiece(target - 8, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_BLACK)
            }else{
                setPiece(target + 8, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_WHITE)
            }
        }

        // Cofnięcie przemieszczenia wieży przy roszadzie (król został już cofnięty wyżej)
        if(isCastling){
            when(target){
                BoardConstants.SQUARE_G1 ->{
                    removePiece(BoardConstants.SQUARE_F1, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_H1, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_C1 ->{
                    removePiece(BoardConstants.SQUARE_D1, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_A1, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_G8 ->{
                    removePiece(BoardConstants.SQUARE_F8, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_H8, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_C8 ->{
                    removePiece(BoardConstants.SQUARE_D8, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_A8, BoardConstants.PIECE_ROOK, sideToMove)
                }
            }
        }
    }

    /**
     * Sprawdza, czy dane pole jest atakowane przez figury podanego koloru.
     * Wykorzystuje technikę "Odwróconej perspektywy" (Reverse POV).
     *
     * @param square Pole, które sprawdzamy (0..63)
     * @param attackingColor Kolor, który potencjalnie atakuje pole ([BoardConstants.COLOR_WHITE] lub [BoardConstants.COLOR_BLACK])
     * @return `true` jeśli pole jest atakowane, `false` w przeciwnym razie.
     */
    fun isSquareAttacked(square: Int, attackingColor: Int): Boolean {
        val occupancy = colors[BoardConstants.COLOR_WHITE] or colors[BoardConstants.COLOR_BLACK]

        val reverseAttackingColor = attackingColor xor 1
        var attacks = PawnAttacks.attacks[reverseAttackingColor][square] and (pieces[PIECE_PAWN] and colors[attackingColor])
        if (attacks != 0UL) return true
        
        attacks = KnightAttacks.attacks[square] and (pieces[PIECE_KNIGHT] and colors[attackingColor])
        if (attacks != 0UL) return true

        attacks = KingAttacks.attacks[square] and (pieces[PIECE_KING] and colors[attackingColor])
        if (attacks != 0UL) return true

        attacks = BishopAttacks.getAttacks(square, occupancy) and (pieces[PIECE_BISHOP] or pieces[PIECE_QUEEN]) and colors[attackingColor]
        if (attacks != 0UL) return true

        attacks = RookAttacks.getAttacks(square, occupancy) and (pieces[PIECE_ROOK] or pieces[PIECE_QUEEN]) and colors[attackingColor]
        return attacks != 0UL
    }
}