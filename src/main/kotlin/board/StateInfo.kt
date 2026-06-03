package board

/**
 * Lekka klasa przechowująca informacje o stanie gry,
 * które są nieodwracalne (trudne do odtworzenia przy cofaniu ruchu).
 * Utworzymy tablicę tych obiektów, by unikać alokacji pamięci podczas gry.
 */
class StateInfo {
    var castlingRights: Int = 0
    var enPassantSquare: Int = -1
    var halfMoveClock: Int = 0
    var capturedPiece: Int = -1
}