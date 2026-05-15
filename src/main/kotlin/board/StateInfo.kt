package board

/**
 * Lekka klasa przechowująca informacje o stanie gry,
 * które są nieodwracalne (trudne do odtworzenia przy cofaniu ruchu).
 * Utworzymy tablicę tych obiektów, by unikać alokacji pamięci podczas gry.
 */
class StateInfo {
    var castlingRights: Int = 0 // 4 bity: np. 1 = biały król, 2 = biała hetmańska, 4 = czarny król, 8 = czarna hetmańska
    var enPassantSquare: Int = -1 // -1 oznacza brak pola do bicia w przelocie
    var halfMoveClock: Int = 0 // Licznik do reguły 50 ruchów
    var capturedPiece: Int = -1 // Jaka figura została zbita (żeby wiedzieć, co przywrócić na planszę)
}