package movegen

import board.Bitboard
import board.BoardConstants

/**
 * Generator ataków Pionów.
 * Oblicza z góry wszystkie możliwe ataki (bicia na ukos) dla każdego koloru i każdego z 64 pól.
 * Wyniki są przechowywane w tablicy dla szybkiego dostępu.
 */
object PawnAttacks {

    /** 
     * Dwuwymiarowa tablica przechowująca pre-kalkulowane maski ataków.
     * Pierwszy wymiar to kolor ([BoardConstants.COLOR_WHITE] lub [BoardConstants.COLOR_BLACK]).
     * Drugi wymiar to indeks pola (0..63).
     */
    val attacks = Array(2) { ULongArray(64) }

    private const val notAFile = 0xFEFEFEFEFEFEFEFEUL
    private const val notHFile = 0x7F7F7F7F7F7F7F7FUL

    init {
        for (square in 0..63) {
            attacks[BoardConstants.COLOR_WHITE][square] = maskPawnAttacks(BoardConstants.COLOR_WHITE, square)
            attacks[BoardConstants.COLOR_BLACK][square] = maskPawnAttacks(BoardConstants.COLOR_BLACK, square)
        }
    }

    /**
     * Oblicza ataki piona dla danego koloru i pola.
     * Uwzględnia maski chroniące przed zawijaniem ruchów (wrap-around) na brzegach planszy.
     * Ta funkcja generuje *tylko* ataki (bicia), a nie ruchy do przodu.
     *
     * @param color Kolor piona.
     * @param square Indeks pola startowego (0..63).
     * @return Maska bitowa potencjalnych pól docelowych ataku.
     */
    private fun maskPawnAttacks(color: Int, square: Int): ULong {
        var attacksBoard = 0UL
        var bitboard = 0UL
        
        bitboard = Bitboard.setBit(bitboard, square)

        if (color == BoardConstants.COLOR_WHITE){
            attacksBoard = attacksBoard or ((bitboard and notAFile) shl 7)
            attacksBoard = attacksBoard or ((bitboard and notHFile) shl 9)
        }
        if (color == BoardConstants.COLOR_BLACK){
            attacksBoard = attacksBoard or ((bitboard and notAFile) shr 7)
            attacksBoard = attacksBoard or ((bitboard and notHFile) shr 9)
        }

        return attacksBoard
    }
}