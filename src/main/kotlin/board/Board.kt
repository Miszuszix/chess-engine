package board

import board.BoardConstants.pieceBishop
import board.BoardConstants.pieceKnight
import board.BoardConstants.piecePawn
import board.BoardConstants.pieceRook
import board.BoardConstants.pieceQueen
import board.BoardConstants.pieceKing
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
     * @param attackingColor Kolor, który potencjalnie atakuje pole (BoardConstants.colorWhite lub colorBlack)
     * @return `true` jeśli pole jest atakowane, `false` w przeciwnym razie.
     */
    fun isSquareAttacked(square: Int, attackingColor: Int): Boolean {
        // Zmienna przechowująca wszystkie figury na planszy (zajętość planszy dla Ray Castingu)
        val occupancy = colors[BoardConstants.colorWhite] or colors[BoardConstants.colorBlack]

        //Pawns
        val reverseAttackingColor = attackingColor xor 1
        var attacks = PawnAttacks.attacks[reverseAttackingColor][square] and (pieces[piecePawn] and colors[attackingColor])
        if (attacks != 0UL) return true
        
        // Knights
        attacks = KnightAttacks.attacks[square] and (pieces[pieceKnight] and colors[attackingColor])
        if (attacks != 0UL) return true

        //Kings
        attacks = KingAttacks.attacks[square] and (pieces[pieceKing] and colors[attackingColor])
        if (attacks != 0UL) return true

        //Bishops and Queen
        attacks = BishopAttacks.getAttacks(square, occupancy) and (pieces[pieceBishop] or pieces[pieceQueen]) and colors[attackingColor]
        if (attacks != 0UL) return true

        //Rooks and Queens
        attacks = RookAttacks.getAttacks(square, occupancy) and (pieces[pieceRook] or pieces[pieceQueen]) and colors[attackingColor]
        return attacks != 0UL
    }
}