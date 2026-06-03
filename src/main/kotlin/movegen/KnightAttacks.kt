package movegen

import board.Bitboard

/**
 * Generator ataków Skoczka (Knight).
 * Oblicza z góry wszystkie możliwe skoki dla każdego z 64 pól
 * i przechowuje je w tablicy w celu natychmiastowego dostępu podczas gry.
 */
object KnightAttacks {

    /** Tablica przechowująca pre-kalkulowane maski ataków dla każdego z 64 pól. */
    val attacks = ULongArray(64)

    private const val notAFile = 0xFEFEFEFEFEFEFEFEUL
    private const val notABFile = 0xFCFCFCFCFCFCFCFCUL
    private const val notHFile = 0x7F7F7F7F7F7F7F7FUL
    private const val notGHFile = 0x3F3F3F3F3F3F3F3FUL

    init {
        for (square in 0..63) {
            attacks[square] = maskKnightAttacks(square)
        }
    }

    /**
     * Oblicza ataki Skoczka z danego pola.
     * Uwzględnia maski chroniące przed zawijaniem ruchów (wrap-around) na brzegach planszy.
     *
     * @param square Indeks pola startowego (0..63).
     * @return Maska bitowa potencjalnych pól docelowych.
     */
    private fun maskKnightAttacks(square: Int): ULong {
        var attacksBoard = 0UL
        var bitboard = 0UL
        
        bitboard = Bitboard.setBit(bitboard, square)
        attacksBoard = attacksBoard or ((bitboard and notHFile) shl 17)
        attacksBoard = attacksBoard or ((bitboard and notAFile) shl 15)
        attacksBoard = attacksBoard or ((bitboard and notGHFile) shl 10)
        attacksBoard = attacksBoard or ((bitboard and notABFile) shl 6)
        
        attacksBoard = attacksBoard or ((bitboard and notAFile) shr 17)
        attacksBoard = attacksBoard or ((bitboard and notHFile) shr 15)
        attacksBoard = attacksBoard or ((bitboard and notABFile) shr 10)
        attacksBoard = attacksBoard or ((bitboard and notGHFile) shr 6)
        
        return attacksBoard
    }
}