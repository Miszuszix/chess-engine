package movegen

import board.Bitboard
import kotlin.math.abs

/**
 * Generator promieni (Ray Casting) dla figur liniowych (Wieża, Goniec, Hetman).
 * Pre-kalkuluje maski promieni rozchodzących się w 8 kierunkach dla każdego z 64 pól.
 * Służy jako podstawa do obliczania ataków "w locie" (on-the-fly) zależnych od zajętości planszy.
 * 
 * Kierunki (indeksy w tablicy):
 * 0 = Północ (N)
 * 1 = Północny-Wschód (NE)
 * 2 = Wschód (E)
 * 3 = Południowy-Wschód (SE)
 * 4 = Południe (S)
 * 5 = Południowy-Zachód (SW)
 * 6 = Zachód (W)
 * 7 = Północny-Zachód (NW)
 */
object Rays {

    /** 
     * Dwuwymiarowa tablica przechowująca pre-kalkulowane promienie.
     * Pierwszy wymiar to kierunek (0..7).
     * Drugi wymiar to indeks pola startowego (0..63).
     */
    val rays = Array(8) { ULongArray(64) }

    init {
        for (square in 0..63) {
            rays[0][square] = generateRay(square, 8)   // N
            rays[1][square] = generateRay(square, 9)   // NE
            rays[2][square] = generateRay(square, 1)   // E
            rays[3][square] = generateRay(square, -7)  // SE
            rays[4][square] = generateRay(square, -8)  // S
            rays[5][square] = generateRay(square, -9)  // SW
            rays[6][square] = generateRay(square, -1)  // W
            rays[7][square] = generateRay(square, 7)   // NW
        }
    }

    /**
     * Oblicza maskę bitową promienia rozchodzącego się od zadanego pola w określonym kierunku.
     * Promień *nie* obejmuje pola startowego. Zatrzymuje się włącznie na krawędzi planszy.
     *
     * @param square Indeks pola startowego (0..63).
     * @param step Wartość kroku definiująca kierunek (np. +8 dla Północy, +1 dla Wschodu).
     * @return Maska bitowa reprezentująca pola na linii promienia.
     */
    private fun generateRay(square: Int, step: Int): ULong {
        var rayBoard = 0UL
        var currentSquare = square
        while (true){
            currentSquare += step
            if (currentSquare !in 0..63) break
            
            // Weryfikacja, czy krok nie "zawinął" promienia na przeciwległą krawędź planszy
            val oldRow = (currentSquare - step) / 8
            val oldColumn = (currentSquare - step) % 8
            val newRow = currentSquare / 8
            val newColumn = currentSquare % 8
            if (abs(oldRow - newRow) > 1 || abs(oldColumn - newColumn) > 1) break

            rayBoard = Bitboard.setBit(rayBoard, currentSquare)
        }
        
        return rayBoard
    }
}