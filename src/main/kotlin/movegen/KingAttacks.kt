package movegen

import board.Bitboard

/**
 * Generator ataków Króla (King).
 * Oblicza z góry wszystkie możliwe ruchy króla dla każdego z 64 pól
 * i przechowuje je w tablicy w celu natychmiastowego dostępu podczas gry.
 */
object KingAttacks {

    val attacks = ULongArray(64)

    private const val notAFile = 0xFEFEFEFEFEFEFEFEUL
    private const val notHFile = 0x7F7F7F7F7F7F7F7FUL

    init {
        for (square in 0..63) {
            attacks[square] = maskKingAttacks(square)
        }
    }

    /**
     * Generuje maskę ataków króla dla danego pola na szachownicy.
     */
    private fun maskKingAttacks(square: Int): ULong {
        var attacksBoard = 0UL
        var bitboard = 0UL
        
        bitboard = Bitboard.setBit(bitboard, square)

        attacksBoard = attacksBoard or (bitboard shl 8)
        attacksBoard = attacksBoard or ((bitboard and notHFile) shl 9)
        attacksBoard = attacksBoard or ((bitboard and notHFile) shl 1)
        attacksBoard = attacksBoard or ((bitboard and notHFile) shr 7)
        attacksBoard = attacksBoard or (bitboard shr 8)
        attacksBoard = attacksBoard or ((bitboard and notAFile) shr 9)
        attacksBoard = attacksBoard or ((bitboard and notAFile) shr 1)
        attacksBoard = attacksBoard or ((bitboard and notAFile) shl 7)

        return attacksBoard
    }
}