package movegen

import board.Bitboard
import board.BoardConstants

/**
 * Generator ataków Pionów.
 * Z uwagi na to, że piony biją tylko do przodu (zależnie od koloru),
 * tablica ataków jest dwuwymiarowa: attacks [kolor][pole].
 */
object PawnAttacks {

    val attacks = Array(2) { ULongArray(64) }

    private const val notAFile = 0xFEFEFEFEFEFEFEFEUL
    private const val notHFile = 0x7F7F7F7F7F7F7F7FUL

    init {
        for (square in 0..63) {
            attacks[BoardConstants.COLOR_WHITE][square] = maskPawnAttacks(BoardConstants.COLOR_WHITE, square)
            attacks[BoardConstants.COLOR_BLACK][square] = maskPawnAttacks(BoardConstants.COLOR_BLACK, square)
        }
    }

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