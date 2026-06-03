package board

/**
 * Zbiór narzędzi do niskopoziomowych operacji na 64-bitowych liczbach (ULong),
 * które pełnią funkcję bitboardów (reprezentacji planszy).
 * Odpowiada za bezpieczne ustawianie, gaszenie i sprawdzanie poszczególnych bitów.
 */
object Bitboard {

    /**
     * Ustawia bit na podanym indeksie [squareIndex] na 1.
     *
     * @param bitboard Wartość bitboardu przed modyfikacją.
     * @param squareIndex Indeks pola, dla którego bit ma zostać ustawiony na 1 (0..63).
     * @return Nowy bitboard z ustawionym bitem.
     */
    fun setBit(bitboard: ULong, squareIndex: Int): ULong {
        val number = (1UL shl squareIndex)
        return bitboard or number
    }

    /**
     * Zeruje bit na podanym indeksie [squareIndex].
     *
     * @param bitboard Wartość bitboardu przed modyfikacją.
     * @param squareIndex Indeks pola, dla którego bit ma zostać wyzerowany (0..63).
     * @return Nowy bitboard z wyzerowanym bitem.
     */
    fun clearBit(bitboard: ULong, squareIndex: Int): ULong {
        val number = (1UL shl squareIndex).inv()
        return bitboard and number
    }

    /**
     * Odczytuje stan bitu na podanym indeksie [squareIndex].
     * Zwraca `true` jeśli bit jest ustawiony na 1, w przeciwnym razie `false`.
     *
     * @param bitboard Wartość bitboardu do sprawdzenia.
     * @param squareIndex Indeks pola, dla którego stan bitu jest odczytywany (0..63).
     * @return `true` jeśli bit jest zapalony (1), `false` w przeciwnym razie (0).
     */
    fun getBit(bitboard: ULong, squareIndex: Int): Boolean {
        val number = bitboard shr squareIndex
        return number and 1UL == 1UL
    }
}