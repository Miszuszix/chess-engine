package board

/**
 * Główna klasa reprezentująca stan szachownicy.
 * Wykorzystuje architekturę Bitboardów dla maksymalnej wydajności.
 * Zamiast alokować nowe obiekty przy każdym ruchu, przechowuje historię
 * w pre-alokowanej tablicy [stateHistory] obsługiwanej indeksem [ply].
 */
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
     * Umieszcza figurę na szachownicy.
     * Aktualizuje jednocześnie bitboard odpowiedniego typu figury oraz jej koloru.
     */
    fun setPiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.setBit(pieces[piece], square)
        colors[color] = Bitboard.setBit(colors[color], square)
    }

    /**
     * Zdejmuje figurę z szachownicy.
     * Czyści odpowiedni bit zarówno w bitboardzie typu figury, jak i w bitboardzie koloru.
     */
    fun removePiece(square: Int, piece: Int, color: Int) {
        pieces[piece] = Bitboard.clearBit(pieces[piece], square)
        colors[color] = Bitboard.clearBit(colors[color], square)
    }

}