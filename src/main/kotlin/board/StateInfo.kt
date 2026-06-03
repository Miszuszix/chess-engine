package board

/**
 * Lekka klasa przechowująca informacje o stanie gry,
 * które są nieodwracalne (trudne do odtworzenia przy cofaniu ruchu).
 * Utworzymy tablicę tych obiektów, by unikać alokacji pamięci podczas gry.
 */
class StateInfo {
    /** 
     * Prawa do roszady (4 bity).
     * Np. 1 = biała krótka, 2 = biała długa, 4 = czarna krótka, 8 = czarna długa.
     */
    var castlingRights: Int = 0
    
    /** 
     * Pole do bicia w przelocie (en passant). 
     * Wartość -1 oznacza brak takiego pola w bieżącym stanie gry. 
     */
    var enPassantSquare: Int = -1
    
    /** 
     * Licznik półruchów od ostatniego pchnięcia piona lub bicia. 
     * Wykorzystywany do weryfikacji reguły 50 ruchów. 
     */
    var halfMoveClock: Int = 0
    
    /** 
     * Typ figury, która została zbita w bieżącym ruchu.
     * Wartość -1 oznacza, że w tym ruchu nie zbito żadnej figury.
     */
    var capturedPiece: Int = -1
}