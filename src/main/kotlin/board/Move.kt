package board

/**
 * Obiekt odpowiedzialny za kodowanie i dekodowanie ruchów
 * z 32-bitowej liczby całkowitej (Int).
 */
object Move {

    /**
     * 
     * Podpowiedź: Użyj operatora przesunięcia bitowego w lewo (`shl`) 
     * dla każdej wartości o odpowiednią liczbę bitów, a następnie
     * połącz wszystko operatorem bitowego LUB (`or`).
     * Dla flag (Boolean), zamień je najpierw na 1 lub 0 (np. `if (isCapture) 1 else 0`).
     */
    //•
    //Bity 0-5 (6 bitów): Skąd ruch (0-63, indeks pola)
    //•
    //Bity 6-11 (6 bitów): Dokąd ruch (0-63)
    //•
    //Bity 12-14 (3 bity): Jaka figura się rusza (0-5, z Twojego Constants.kt)
    //•
    //Bity 15-17 (3 bity): Do jakiej figury promujemy (1-4, domyślnie 0 jeśli brak promocji)
    //•
    //Bit 18 (1 bit): Flaga bicia (Capture)
    //•
    //Bit 19 (1 bit): Flaga podwójnego skoku piona (Double Pawn Push)
    //•
    //Bit 20 (1 bit): Flaga bicia w przelocie (En Passant)
    //•
    //Bit 21 (1 bit): Flaga roszady (Castling)
    fun encode(
        sourceSquare: Int,
        targetSquare: Int,
        piece: Int,
        promotedPiece: Int = 0,
        isCapture: Boolean = false,
        isDoublePawnPush: Boolean = false,
        isEnPassant: Boolean = false,
        isCastling: Boolean = false
    ): Int {
        val castling = if (isCastling) 1 else 0
        val enPassant = if (isEnPassant) 1 else 0
        val doublePawnPush = if (isDoublePawnPush) 1 else 0
        val capture = if (isCapture) 1 else 0
        var result: Int = castling
        result = result shl 1 or enPassant
        result = result shl 1 or doublePawnPush
        result = result shl 1 or capture
        result = result shl 3 or promotedPiece
        result = result shl 3 or piece
        result = result shl 6 or targetSquare
        result = result shl 6 or sourceSquare
        return result
    }

    /**
     * Podpowiedź: Aby wyciągnąć bity 0-5, nie musisz przesuwać liczby. 
     * Wystarczy użyć maski bitowej z operatorem `and`. 
     * Maska dla 6 bitów to 0x3F (dziesiętnie 63).
     */
    fun getSourceSquare(move: Int): Int {
        return move and 0x3F
    }

    /**
     * Podpowiedź: Przesuń liczbę w prawo (`shr`) o 6 bitów, 
     * a następnie nałóż maskę 0x3F za pomocą `and`.
     */
    fun getTargetSquare(move: Int): Int {
        return (move shr 6) and 0x3F
    }

    /**
     * Podpowiedź: Przesuń w prawo o 12. Maska dla 3 bitów to 0x7 (binarnie 111).
     */
    fun getPiece(move: Int): Int {
        return (move shr 12) and 0x7
    }

    /**
     */
    fun getPromotedPiece(move: Int): Int {
        return (move shr 15) and 0x7
    }

    /**
     * Podpowiedź: Zwróć Boolean. Zastosuj maskę `and 1`, a następnie przyrównaj do 1 (`== 1`)
     * lub użyj `and 0x40000` (w zależności od tego, jak Ci wygodniej).
     */
    fun isCapture(move: Int): Boolean {
        return (move shr 18) and 1 == 1
    }

    /**
     */
    fun isDoublePawnPush(move: Int): Boolean {
        return (move shr 19) and 1 == 1
    }

    /**
     */
    fun isEnPassant(move: Int): Boolean {
        return (move shr 20) and 1 == 1
    }

    /**
     */
    fun isCastling(move: Int): Boolean {
        return (move shr 21) and 1 == 1
    }
}