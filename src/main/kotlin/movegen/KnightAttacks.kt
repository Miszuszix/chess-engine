package movegen

import board.Bitboard

/**
 * Generator ataków Skoczka (Knight).
 * Oblicza z góry wszystkie możliwe skoki dla każdego z 64 pól
 * i przechowuje je w tablicy w celu natychmiastowego dostępu podczas gry.
 */
object KnightAttacks {

    // Tablica przechowująca gotowe maski ataków dla każdego z 64 pól
    val attacks = ULongArray(64)

    // Maski kolumn potrzebne do uniknięcia zawijania planszy (wrap-around)
    // notAFile gasi wszystkie jedynki w kolumnie A. notABFile w kolumnach A i B, itd.
    private const val notAFile = 0xFEFEFEFEFEFEFEFEUL
    private const val notABFile = 0xFCFCFCFCFCFCFCFCUL
    private const val notHFile = 0x7F7F7F7F7F7F7F7FUL
    private const val notGHFile = 0x3F3F3F3F3F3F3F3FUL

    init {
        // Podczas ładowania obiektu do pamięci, obliczamy ataki dla każdego pola
        for (square in 0..63) {
            attacks[square] = maskKnightAttacks(square)
        }
    }

    /**
     * Generuje maskę ataków skoczka dla danego pola, dbając o to, by skoki nie "zawijały" się na krawędziach.
     */
    private fun maskKnightAttacks(square: Int): ULong {
        var attacksBoard = 0UL
        var bitboard = 0UL
        
        // Stawiamy skoczka na odpowiednim polu
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