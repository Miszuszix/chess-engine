package movegen

/**
 * Generator ataków Wieży w locie (na podstawie zajętości planszy).
 */
object RookAttacks {

    /**
     * Zwraca maskę bitową ataków Wieży dla danego pola, uwzględniając blokujące figury.
     * Wykorzystuje wcześniej wygenerowane promienie (ray casting).
     *
     * @param square Indeks pola, na którym stoi Wieża (0..63).
     * @param occupancy Bitboard reprezentujący wszystkie figury na planszy (zajętość pól).
     * @return Maska bitowa pól atakowanych przez Wieżę z podanego pola.
     */
    fun getAttacks(square: Int, occupancy: ULong): ULong {
        var attacks = 0UL
        // Kierunki dla Wieży: 0 (N), 2 (E), 4 (S), 6 (W)
        for(direction in 0..6 step 2){
            val ray = Rays.rays[direction][square]
            val blockers = ray and occupancy
            if (blockers != 0UL){
                // Zależnie od kierunku szukamy pierwszego blokera skanując od najstarszego bitu
                // lub od najmłodszego bitu.
                val blockerIndex = if (direction == 4 || direction == 6){
                    63 - blockers.countLeadingZeroBits()
                }else{
                    blockers.countTrailingZeroBits()
                }
                // Ataki to promień odblokowany (ray) odjąć promień "za blokerem" (promień blokera)
                attacks = attacks or (ray xor Rays.rays[direction][blockerIndex])
            }else{
                attacks = attacks or ray
            }
        }
        return attacks
    }
}