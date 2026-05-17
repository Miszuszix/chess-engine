package board

/**
 * Centralny "słownik" silnika szachowego.
 * Przechowuje stałe wartości liczbowe dla kolorów, typów figur
 * oraz indeksów 64 pól szachownicy w celu uniknięcia "magicznych liczb" w kodzie.
 */
object BoardConstants {
    // Colors
    const val COLOR_WHITE = 0
    const val COLOR_BLACK = 1

    // Piece Types
    const val PIECE_PAWN = 0
    const val PIECE_KNIGHT = 1
    const val PIECE_BISHOP = 2
    const val PIECE_ROOK = 3
    const val PIECE_QUEEN = 4
    const val PIECE_KING = 5

    // Squares
    const val SQUARE_A1 = 0
    const val SQUARE_B1 = 1
    const val SQUARE_C1 = 2
    const val SQUARE_D1 = 3
    const val SQUARE_E1 = 4
    const val SQUARE_F1 = 5
    const val SQUARE_G1 = 6
    const val SQUARE_H1 = 7

    const val SQUARE_A2 = 8
    const val SQUARE_B2 = 9
    const val SQUARE_C2 = 10
    const val SQUARE_D2 = 11
    const val SQUARE_E2 = 12
    const val SQUARE_F2 = 13
    const val SQUARE_G2 = 14
    const val SQUARE_H2 = 15

    const val SQUARE_A3 = 16
    const val SQUARE_B3 = 17
    const val SQUARE_C3 = 18
    const val SQUARE_D3 = 19
    const val SQUARE_E3 = 20
    const val SQUARE_F3 = 21
    const val SQUARE_G3 = 22
    const val SQUARE_H3 = 23

    const val SQUARE_A4 = 24
    const val SQUARE_B4 = 25
    const val SQUARE_C4 = 26
    const val SQUARE_D4 = 27
    const val SQUARE_E4 = 28
    const val SQUARE_F4 = 29
    const val SQUARE_G4 = 30
    const val SQUARE_H4 = 31

    const val SQUARE_A5 = 32
    const val SQUARE_B5 = 33
    const val SQUARE_C5 = 34
    const val SQUARE_D5 = 35
    const val SQUARE_E5 = 36
    const val SQUARE_F5 = 37
    const val SQUARE_G5 = 38
    const val SQUARE_H5 = 39

    const val SQUARE_A6 = 40
    const val SQUARE_B6 = 41
    const val SQUARE_C6 = 42
    const val SQUARE_D6 = 43
    const val SQUARE_E6 = 44
    const val SQUARE_F6 = 45
    const val SQUARE_G6 = 46
    const val SQUARE_H6 = 47

    const val SQUARE_A7 = 48
    const val SQUARE_B7 = 49
    const val SQUARE_C7 = 50
    const val SQUARE_D7 = 51
    const val SQUARE_E7 = 52
    const val SQUARE_F7 = 53
    const val SQUARE_G7 = 54
    const val SQUARE_H7 = 55

    const val SQUARE_A8 = 56
    const val SQUARE_B8 = 57
    const val SQUARE_C8 = 58
    const val SQUARE_D8 = 59
    const val SQUARE_E8 = 60
    const val SQUARE_F8 = 61
    const val SQUARE_G8 = 62
    const val SQUARE_H8 = 63
}
