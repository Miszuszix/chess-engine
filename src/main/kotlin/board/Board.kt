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

    // Bitboardy dla każdego typu figury (Pion = 0, skoczek = 1, Goniec = 2, Wieża = 3, Hetman = 4, Król = 5)
    val pieces = ULongArray(6)

    // Bitboardy dla kolorów (Biały = 0, Czarny = 1)
    val colors = ULongArray(2)

    // Pre-alokowana historia stanu (zakładamy maksymalnie 1024 półruchy w partii)
    val stateHistory = Array(1024) { StateInfo() }

    // Aktualny numer półruchu od początku partii (służy m.in. jako indeks do stateHistory)
    var currentHalfMove = 0
    /**
     * Umieszcza figurę na szachownicy.
     * Aktualizuje jednocześnie bitboard odpowiedniego typu figury oraz jej koloru.
     */
    fun setPiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.setBit(pieces[piece], square)
        colors[color] = Bitboard.setBit(colors[color], square)
    }

    /**
     * Zdejmuje figurę z szachownicy.
     * Czyści odpowiedni bit zarówno w bitboardzie typu figury, jak i w bitboardzie koloru.
     */
    fun removePiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.clearBit(pieces[piece], square)
        colors[color] = Bitboard.clearBit(colors[color], square)
    }

    /**
     * Sprawdza, czy dane pole jest atakowane przez figury podanego koloru.
     * Wykorzystuje technikę "Odwróconej perspektywy" (Reverse POV).
     *
     * @param square Pole, które sprawdzamy (0..63)
     * @param attackingColor Kolor, który potencjalnie atakuje pole (BoardConstants.COLOR_WHITE lub COLOR_BLACK)
     * @return `true` jeśli pole jest atakowane, `false` w przeciwnym razie.
     */
    fun isSquareAttacked(square: Int, attackingColor: Int): Boolean {
        // Zmienna przechowująca wszystkie figury na planszy (zajętość planszy dla Ray Castingu)
        val occupancy = colors[BoardConstants.COLOR_WHITE] or colors[BoardConstants.COLOR_BLACK]

        //Pawns
        val reverseAttackingColor = attackingColor xor 1
        var attacks = PawnAttacks.attacks[reverseAttackingColor][square] and (pieces[PIECE_PAWN] and colors[attackingColor])
        if (attacks != 0UL) return true
        
        // Knights
        attacks = KnightAttacks.attacks[square] and (pieces[PIECE_KNIGHT] and colors[attackingColor])
        if (attacks != 0UL) return true

        //Kings
        attacks = KingAttacks.attacks[square] and (pieces[PIECE_KING] and colors[attackingColor])
        if (attacks != 0UL) return true

        //Bishops and Queen
        attacks = BishopAttacks.getAttacks(square, occupancy) and (pieces[PIECE_BISHOP] or pieces[PIECE_QUEEN]) and colors[attackingColor]
        if (attacks != 0UL) return true

        //Rooks and Queens
        attacks = RookAttacks.getAttacks(square, occupancy) and (pieces[PIECE_ROOK] or pieces[PIECE_QUEEN]) and colors[attackingColor]
        return attacks != 0UL
    }
}