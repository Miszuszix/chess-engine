package board

/**
 * Centralny "słownik" silnika szachowego.
 * Przechowuje stałe wartości liczbowe dla kolorów, typów figur
 * oraz indeksów 64 pól szachownicy w celu uniknięcia "magicznych liczb" w kodzie.
 */
object BoardConstants {
    // Colors
    const val colorWhite = 0
    const val colorBlack = 1

    // Piece Types
    const val piecePawn = 0
    const val pieceKnight = 1
    const val pieceBishop = 2
    const val pieceRook = 3
    const val pieceQueen = 4
    const val pieceKing = 5

    // Squares
    const val squareA1 = 0
    const val squareB1 = 1
    const val squareC1 = 2
    const val squareD1 = 3
    const val squareE1 = 4
    const val squareF1 = 5
    const val squareG1 = 6
    const val squareH1 = 7

    const val squareA2 = 8
    const val squareB2 = 9
    const val squareC2 = 10
    const val squareD2 = 11
    const val squareE2 = 12
    const val squareF2 = 13
    const val squareG2 = 14
    const val squareH2 = 15

    const val squareA3 = 16
    const val squareB3 = 17
    const val squareC3 = 18
    const val squareD3 = 19
    const val squareE3 = 20
    const val squareF3 = 21
    const val squareG3 = 22
    const val squareH3 = 23

    const val squareA4 = 24
    const val squareB4 = 25
    const val squareC4 = 26
    const val squareD4 = 27
    const val squareE4 = 28
    const val squareF4 = 29
    const val squareG4 = 30
    const val squareH4 = 31

    const val squareA5 = 32
    const val squareB5 = 33
    const val squareC5 = 34
    const val squareD5 = 35
    const val squareE5 = 36
    const val squareF5 = 37
    const val squareG5 = 38
    const val squareH5 = 39

    const val squareA6 = 40
    const val squareB6 = 41
    const val squareC6 = 42
    const val squareD6 = 43
    const val squareE6 = 44
    const val squareF6 = 45
    const val squareG6 = 46
    const val squareH6 = 47

    const val squareA7 = 48
    const val squareB7 = 49
    const val squareC7 = 50
    const val squareD7 = 51
    const val squareE7 = 52
    const val squareF7 = 53
    const val squareG7 = 54
    const val squareH7 = 55

    const val squareA8 = 56
    const val squareB8 = 57
    const val squareC8 = 58
    const val squareD8 = 59
    const val squareE8 = 60
    const val squareF8 = 61
    const val squareG8 = 62
    const val squareH8 = 63
}
