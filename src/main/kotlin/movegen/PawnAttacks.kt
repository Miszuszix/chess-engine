package movegen

import board.Bitboard
import board.BoardConstants

/**
 * Generator ataków Pionów.
 * Z uwagi na to, że piony biją tylko do przodu (zależnie od koloru),
 * tablica ataków jest dwuwymiarowa: attacks[kolor][pole].
 */
object PawnAttacks {

    // Tablica ataków: 2 kolory, po 64 pola
    val attacks = Array(2) { ULongArray(64) }

    // Maski kolumn chroniące przed "zawijaniem" się ataków z lewej i prawej krawędzi
    private const val notAFile = 0xFEFEFEFEFEFEFEFEUL
    private const val notHFile = 0x7F7F7F7F7F7F7F7FUL

    init {
        for (square in 0..63) {
            attacks[BoardConstants.COLOR_WHITE][square] = maskPawnAttacks(BoardConstants.COLOR_WHITE, square)
            attacks[BoardConstants.COLOR_BLACK][square] = maskPawnAttacks(BoardConstants.COLOR_BLACK, square)
        }
    }

    /**
     * Generuje maskę ataków piona dla danego koloru i pola.
     * UWAGA: Generujemy tutaj *tylko ataki* (bicia na ukos), nie generujemy zwykłych ruchów do przodu!
     * Zwykłe pchnięcia piona będziemy obsługiwać dynamicznie w generatorze ruchów.
     *
     * Podpowiedź:
     * 1. Utwórz zmienną `bitboard` i ustaw na niej jeden bit na indeksie `square`.
     * 2. Utwórz zmienną `attacksBoard = 0UL`.
     * 3. Sprawdź kolor (użyj `if (color == BoardConstants.COLOR_WHITE)`).
     * 4. Dla białych:
     *    - Atak w lewo-skos (North-West): `bitboard and notAFile shl 7` (dodaj to do attacksBoard używając `or`).
     *    - Atak w prawo-skos (North-East): `bitboard and notHFile shl 9`.
     * 5. Dla czarnych:
     *    - Atak w prawo-skos / South-East (spadek indeksu o 7): `bitboard and notHFile shr 7`.
     *    - Atak w lewo-skos / South-West (spadek indeksu o 9): `bitboard and notAFile shr 9`.
     * 6. Zwróć `attacksBoard`.
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