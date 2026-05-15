package movegen

import board.Bitboard

/**
 * Generator promieni (Ray Casting) dla figur liniowych.
 * Pre-kalkuluje maski promieni rozchodzących się w 8 kierunkach dla każdego z 64 pól.
 * Kierunki indeksujemy następująco:
 * 0 = Północ (N)
 * 1 = Południe (S)
 * 2 = Wschód (E)
 * 3 = Zachód (W)
 * 4 = Północny-Zachód (NW)
 * 5 = Północny-Wschód (NE)
 * 6 = Południowy-Zachód (SW)
 * 7 = Południowy-Wschód (SE)
 */
object Rays {

    // Dwuwymiarowa tablica promieni: rays[kierunek][pole_startowe]
    val rays = Array(8) { ULongArray(64) }

    init {
        for (square in 0..63) {
            rays[0][square] = generateRay(square, 8)   // N: +8
            rays[1][square] = generateRay(square, -8)  // S: -8
            rays[2][square] = generateRay(square, 1)   // E: +1
            rays[3][square] = generateRay(square, -1)  // W: -1
            rays[4][square] = generateRay(square, 7)   // NW: +7
            rays[5][square] = generateRay(square, 9)   // NE: +9
            rays[6][square] = generateRay(square, -9)  // SW: -9
            rays[7][square] = generateRay(square, -7)  // SE: -7
        }
    }

    /**
     * Generuje promień dla podanego pola i kroku (kierunku).
     * Promień biegnie od pola startowego (wyłącznie) aż do krawędzi planszy włącznie.
     */
    private fun generateRay(square: Int, step: Int): ULong {
        var rayBoard = 0UL
        var currentSquare = square

        // TODO: Zaimplementuj pętlę generującą promień.
        // Podpowiedź:
        // Użyj pętli while. Dodawaj `step` do `currentSquare`.
        // Przed dodaniem bitu do `rayBoard` (używając Bitboard.setBit), musisz sprawdzić 2 rzeczy:
        // 1. Czy `currentSquare` po dodaniu kroku wciąż jest na planszy (0..63)?
        // 2. Czy nie nastąpiło "zawinięcie" planszy? 
        //    (np. idąc na Wschód (+1) z pola H1 (7) nie możemy przejść na A2 (8)).
        //    Możesz to sprawdzić weryfikując, czy odległość między kolumnami 
        //    lub rzędami starego i nowego pola nie jest drastycznie duża.
        
        return rayBoard
    }
}