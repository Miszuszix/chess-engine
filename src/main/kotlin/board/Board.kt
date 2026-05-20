package board

import board.BoardConstants.PIECE_BISHOP
import board.BoardConstants.PIECE_KNIGHT
import board.BoardConstants.PIECE_PAWN
import board.BoardConstants.PIECE_ROOK
import board.BoardConstants.PIECE_QUEEN
import board.BoardConstants.PIECE_KING
import movegen.PawnAttacks
import movegen.KnightAttacks
import movegen.KingAttacks
import movegen.BishopAttacks
import movegen.RookAttacks

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

    // Aktualny numer półruchu od początku partii (służy m.in. jako indeks do stateHistory)
    var currentHalfMove = 0

    // Kto wykonuje teraz ruch (0 = Białe, 1 = Czarne)
    var sideToMove = BoardConstants.COLOR_WHITE

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

    /**
     * Wykonuje ruch na planszy (Make Move).
     * Aktualizuje bitboardy, przesuwa historię stanu (ply) i zmienia turę.
     */
    fun makeMove(move: Int) {
        val source = Move.getSourceSquare(move)
        val target = Move.getTargetSquare(move)
        val piece = Move.getPiece(move)
        val isCapture = Move.isCapture(move)
        val isDoublePawnPush = Move.isDoublePawnPush(move)
        val isEnPassant = Move.isEnPassant(move)
        val isCastling = Move.isCastling(move)
        val promotedPiece = Move.getPromotedPiece(move)

        // Pobieramy aktualny stan z historii i "czyste pudełko" na nowy stan
        val currentState = stateHistory[currentHalfMove]
        val nextState = stateHistory[currentHalfMove + 1]

        // 1. Klonowanie stanu nieodwracalnego do nowego "pudełka"
        nextState.castlingRights = currentState.castlingRights
        nextState.halfMoveClock = currentState.halfMoveClock + 1
        nextState.enPassantSquare = -1 // Domyślnie brak pola do bicia w przelocie w nowym ruchu
        nextState.capturedPiece = -1

        // 2. Resetujemy licznik 50 ruchów jeśli ruch to bicie lub pchnięcie piona
        if (isCapture || piece == BoardConstants.PIECE_PAWN) {
            nextState.halfMoveClock = 0
        }

        // 3. Zwiększamy indeks głębokości (wchodzimy głębiej w drzewo/partię)
        currentHalfMove++

        // TODO: 2. Obsługa bicia STANDARDOWEGO (isCapture == true ORAZ isEnPassant == false).
        // Jeśli to zwykłe bicie, musisz:
        // a) Znaleźć, jaka figura przeciwnika stała na polu 'target'.
        //    (Podpowiedź: przeiteruj od 0 do 5 i sprawdź za pomocą Bitboard.getBit, w której tablicy pieces na polu 'target' jest 1).
        // b) Zapisać typ zbitej figury do `nextState.capturedPiece`.
        // c) Zdjąć przeciwnikowi tę figurę z pola 'target'.
        if(isCapture && !isEnPassant){
            for(capturedPiece in 0..5){
                if(Bitboard.getBit(pieces[capturedPiece], target)){
                    nextState.capturedPiece = capturedPiece
                    removePiece(target, capturedPiece, sideToMove xor 1)
                }
            }
        }

        if(isCapture && isEnPassant){
            nextState.capturedPiece = BoardConstants.PIECE_PAWN
            if (sideToMove == BoardConstants.COLOR_WHITE) {
                removePiece(target - 8, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_BLACK)
            } else {
                removePiece(target + 8, BoardConstants.PIECE_PAWN, BoardConstants.COLOR_WHITE)
            }
        }

        // TODO: 1. Zdejmij figurę 'piece' z pola 'source' i postaw ją na polu 'target'
        // dla aktualnego koloru (zmienna 'sideToMove'). Wykorzystaj swoje metody z tej klasy!
        //zmieniłem kolejność tego bo najpierw zdejmiemy figure przeciwnika a potem dopiero przesuniemy naszą. Inaczej byśmy zdjęli własną figurę
        removePiece(source, piece, sideToMove)
        setPiece(target, piece, sideToMove)

        if (isCastling){
            when(target){
                BoardConstants.SQUARE_G1 -> {
                    removePiece(BoardConstants.SQUARE_H1, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_F1, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_C1 -> {
                    removePiece(BoardConstants.SQUARE_A1, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_D1, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_G8 -> {
                    removePiece(BoardConstants.SQUARE_H8, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_F8, BoardConstants.PIECE_ROOK, sideToMove)
                }
                BoardConstants.SQUARE_C8 -> {
                    removePiece(BoardConstants.SQUARE_A8, BoardConstants.PIECE_ROOK, sideToMove)
                    setPiece(BoardConstants.SQUARE_D8, BoardConstants.PIECE_ROOK, sideToMove)
                }
            }
        }


        // TODO: 3. Obsługa podwójnego skoku piona (isDoublePawnPush).
        // Jeśli to prawda, musisz udostępnić pole do bicia w przelocie:
        // Ustaw `nextState.enPassantSquare`. Pole to znajduje się dokładnie
        // pomiędzy polem startowym a docelowym. Zastanów się, jak to łatwo wyliczyć matematycznie (np. średnia arytmetyczna?).
        if(isDoublePawnPush){
            if(sideToMove == BoardConstants.COLOR_WHITE){
                nextState.enPassantSquare = source + 8
            }else{
                nextState.enPassantSquare = source - 8
            }
        }

        // TODO: 5. Promocja piona.
        // Sprawdź, czy `promotedPiece` jest różne od 0 (zakładając, że 0 to brak promocji).
        // Jeśli tak, to w poprzednich linijkach nasz silnik fizycznie postawił Piona na polu `target`.
        // Musisz teraz zdjąć tego Piona z `target` i postawić tam nową figurę (`promotedPiece`).


        // TODO: 6. Aktualizacja praw do roszady.
        // Zaktualizuj `nextState.castlingRights`.
        // Użyj logicznego `and` na aktualnej wartości zmiennej oraz odpowiednich maskach
        // z tablicy `BoardConstants.CASTLING_RIGHTS_UPDATE` dla pola `source` i `target`.
        // Np.: nextState.castlingRights = nextState.castlingRights and maska1 and maska2

        
        // TODO: 4. Zmień turę gracza.
        // Użyj operatora XOR (`xor 1`) na zmiennej `sideToMove`, aby szybko przełączyć kolor.
        sideToMove = sideToMove xor 1
    }

    /**
     * Sprawdza, czy dane pole jest atakowane przez figury podanego koloru.
     * Wykorzystuje technikę "Odwróconej perspektywy" (Reverse POV).
     *
     * @param square Pole, które sprawdzamy (0..63)
     * @param attackingColor Kolor, który potencjalnie atakuje pole (BoardConstants.COLOR_WHITE lub COLOR_BLACK)
     * @return `true` jeśli pole jest atakowane, `false` w przeciwnym razie.
     */
    fun isSquareAttacked(square: Int, attackingColor: Int): Boolean {
        // Zmienna przechowująca wszystkie figury na planszy (zajętość planszy dla Ray Castingu)
        val occupancy = colors[BoardConstants.COLOR_WHITE] or colors[BoardConstants.COLOR_BLACK]

        //Pawns
        val reverseAttackingColor = attackingColor xor 1
        var attacks = PawnAttacks.attacks[reverseAttackingColor][square] and (pieces[PIECE_PAWN] and colors[attackingColor])
        if (attacks != 0UL) return true
        
        // Knights
        attacks = KnightAttacks.attacks[square] and (pieces[PIECE_KNIGHT] and colors[attackingColor])
        if (attacks != 0UL) return true

        //Kings
        attacks = KingAttacks.attacks[square] and (pieces[PIECE_KING] and colors[attackingColor])
        if (attacks != 0UL) return true

        //Bishops and Queen
        attacks = BishopAttacks.getAttacks(square, occupancy) and (pieces[PIECE_BISHOP] or pieces[PIECE_QUEEN]) and colors[attackingColor]
        if (attacks != 0UL) return true

        //Rooks and Queens
        attacks = RookAttacks.getAttacks(square, occupancy) and (pieces[PIECE_ROOK] or pieces[PIECE_QUEEN]) and colors[attackingColor]
        return attacks != 0UL
    }
}