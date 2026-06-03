package movegen

/**
 * Generator ataków Hetmana w locie (na podstawie zajętości planszy).
 * Ponieważ hetman porusza się jak wieża i goniec jednocześnie, ten generator
 * po prostu łączy (operatorem bitowym OR) ataki tych dwóch figur.
 */
object QueenAttacks {

    /**
     * Zwraca maskę bitową ataków Hetmana dla danego pola, uwzględniając blokujące figury.
     * Wykorzystuje logikę [BishopAttacks] oraz [RookAttacks].
     *
     * @param square Indeks pola, na którym stoi Hetman (0..63).
     * @param occupancy Bitboard reprezentujący wszystkie figury na planszy (zajętość pól).
     * @return Maska bitowa pól atakowanych przez Hetmana z podanego pola.
     */
    fun getAttacks(square: Int, occupancy: ULong): ULong {
        return BishopAttacks.getAttacks(square, occupancy) or RookAttacks.getAttacks(square, occupancy)
    }
}