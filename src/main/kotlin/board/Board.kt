package board

class Board {

    // Bitboardy dla każdego typu figury (Pion = 0, skoczek = 1, Goniec = 2, Wieża = 3, Hetman = 4, Król = 5)
    val pieces = ULongArray(6)

    // Bitboardy dla kolorów (Biały = 0, Czarny = 1)
    val colors = ULongArray(2)

    // Pre-alokowana historia stanu (zakładamy maksymalnie 1024 półruchy w partii)
    val stateHistory = Array(1024) { StateInfo() }

    // Aktualna głębokość / numer półruchu od początku partii
    var ply = 0

    /**
     * TODO: Zaimplementuj funkcję dodającą figurę na planszę.
     *
     * Podpowiedź: Musisz zaktualizować DWA bitboardy używając `Bitboard.setBit`:
     * 1. Bitboard odpowiedniego typu figury (`pieces[piece]`)
     * 2. Bitboard odpowiedniego koloru (`colors[color]`)
     */
    fun setPiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.setBit(pieces[piece], square)
        colors[color] = Bitboard.setBit(colors[color], square)
    }

    /**
     * TODO: Zaimplementuj funkcję usuwającą figurę z planszy.
     *
     * Podpowiedź: Podobnie jak wyżej, zaktualizuj DWA bitboardy,
     * ale tym razem używając `Bitboard.clearBit`.
     */
    fun removePiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.clearBit(pieces[piece], square)
        colors[color] = Bitboard.clearBit(colors[color], square)
    }

}