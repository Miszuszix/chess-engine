package board

/**
 * Obiekt narzędziowy do kodowania i dekodowania informacji o ruchu
 * w pojedynczej 32-bitowej liczbie całkowitej (Int) dla maksymalnej wydajności.
 *
 * Struktura bitowa ruchu:
 * - Bity 0-5 (6 bitów): Pole źródłowe (0-63)
 * - Bity 6-11 (6 bitów): Pole docelowe (0-63)
 * - Bity 12-14 (3 bity): Typ poruszającej się figury (z [BoardConstants])
 * - Bity 15-17 (3 bity): Typ figury promowanej (jeśli dotyczy)
 * - Bit 18 (1 bit): Flaga bicia
 * - Bit 19 (1 bit): Flaga podwójnego pchnięcia piona
 * - Bit 20 (1 bit): Flaga bicia w przelocie (en passant)
 * - Bit 21 (1 bit): Flaga roszady
 */
object Move {

    /**
     * Koduje informacje o ruchu w 32-bitową liczbę całkowitą.
     *
     * @param sourceSquare Pole startowe ruchu (0-63).
     * @param targetSquare Pole docelowe ruchu (0-63).
     * @param piece Typ poruszającej się figury.
     * @param promotedPiece Typ figury, na którą promowany jest pion (domyślnie 0).
     * @param isCapture Czy ruch jest biciem.
     * @param isDoublePawnPush Czy ruch jest podwójnym pchnięciem piona.
     * @param isEnPassant Czy ruch jest biciem w przelocie.
     * @param isCastling Czy ruch jest roszadą.
     * @return 32-bitowa liczba całkowita reprezentująca ruch.
     */
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
     * Ekstrahuje indeks pola źródłowego z zakodowanego ruchu.
     * @param move Zakodowany ruch.
     * @return Indeks pola źródłowego (0-63).
     */
    fun getSourceSquare(move: Int): Int {
        return move and 0x3F
    }

    /**
     * Ekstrahuje indeks pola docelowego z zakodowanego ruchu.
     * @param move Zakodowany ruch.
     * @return Indeks pola docelowego (0-63).
     */
    fun getTargetSquare(move: Int): Int {
        return (move shr 6) and 0x3F
    }

    /**
     * Ekstrahuje typ poruszającej się figury z zakodowanego ruchu.
     * @param move Zakodowany ruch.
     * @return Typ figury (zgodnie z [BoardConstants]).
     */
    fun getPiece(move: Int): Int {
        return (move shr 12) and 0x7
    }

    /**
     * Ekstrahuje typ promowanej figury z zakodowanego ruchu.
     * @param move Zakodowany ruch.
     * @return Typ promowanej figury lub 0, jeśli brak promocji.
     */
    fun getPromotedPiece(move: Int): Int {
        return (move shr 15) and 0x7
    }

    /**
     * Sprawdza, czy ruch jest biciem.
     * @param move Zakodowany ruch.
     * @return `true`, jeśli ruch jest biciem.
     */
    fun isCapture(move: Int): Boolean {
        return (move shr 18) and 1 == 1
    }

    /**
     * Sprawdza, czy ruch jest podwójnym pchnięciem piona.
     * @param move Zakodowany ruch.
     * @return `true`, jeśli ruch to podwójne pchnięcie piona.
     */
    fun isDoublePawnPush(move: Int): Boolean {
        return (move shr 19) and 1 == 1
    }

    /**
     * Sprawdza, czy ruch jest biciem w przelocie.
     * @param move Zakodowany ruch.
     * @return `true`, jeśli ruch to bicie w przelocie.
     */
    fun isEnPassant(move: Int): Boolean {
        return (move shr 20) and 1 == 1
    }

    /**
     * Sprawdza, czy ruch jest roszadą.
     * @param move Zakodowany ruch.
     * @return `true`, jeśli ruch to roszada.
     */
    fun isCastling(move: Int): Boolean {
        return (move shr 21) and 1 == 1
    }
}