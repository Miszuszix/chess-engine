package movegen

/**
 * Generator ataków Hetmana w locie (na podstawie zajętości planszy).
 */
object QueenAttacks {

    /**
     * Zwraca maskę bitową ataków Hetmana dla danego pola, uwzględniając blokujące figury.
     *
     * @param square Indeks pola, na którym stoi Hetman (0..63)
     * @param occupancy Bitboard reprezentujący wszystkie figury na planszy (zarówno białe jak i czarne)
     */
    fun getAttacks(square: Int, occupancy: ULong): ULong {
        return BishopAttacks.getAttacks(square, occupancy) or RookAttacks.getAttacks(square, occupancy)
    }
}