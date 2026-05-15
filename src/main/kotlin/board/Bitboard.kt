package board

/**
 * Zbiór narzędzi do niskopoziomowych operacji na 64-bitowych liczbach (ULong),
 * które pełnią funkcję bitboardów (reprezentacji planszy).
 * Odpowiada za bezpieczne ustawianie, gaszenie i sprawdzanie poszczególnych bitów.
 */
object Bitboard {

    /**
     * Ustawia bit na podanym indeksie `squareIndex` na 1.
     */
    fun setBit(bitboard: ULong, squareIndex: Int): ULong {
        val number = (1UL shl squareIndex)
        return bitboard or number
    }

    /**
     * Zeruje bit na podanym indeksie `squareIndex`.
     */
    fun clearBit(bitboard: ULong, squareIndex: Int): ULong {
        val number = (1UL shl squareIndex).inv()
        return bitboard and number
    }

    /**
     * Odczytuje stan bitu na podanym indeksie `squareIndex`.
     * Zwraca `true` jeśli bit jest ustawiony na 1, w przeciwnym razie `false`.
     */
    fun getBit(bitboard: ULong, squareIndex: Int): Boolean {
        val number = bitboard shr squareIndex
        return number and 1UL == 1UL
    }
}
