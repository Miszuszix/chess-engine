package movegen

/**
 * Generator ataków Gońca w locie (na podstawie zajętości planszy).
 */
object BishopAttacks {

    /**
     * Zwraca maskę bitową ataków Gońca dla danego pola, uwzględniając blokujące figury.
     * Wykorzystuje wcześniej wygenerowane promienie (ray casting).
     *
     * @param square Indeks pola, na którym stoi Goniec (0..63).
     * @param occupancy Bitboard reprezentujący wszystkie figury na planszy (zajętość pól).
     * @return Maska bitowa pól atakowanych przez Gońca z podanego pola.
     */
    fun getAttacks(square: Int, occupancy: ULong): ULong {
        var attacks = 0UL
        // Kierunki dla Gońca: 1 (NE), 3 (SE), 5 (SW), 7 (NW)
        for(direction in 1..7 step 2){
            val ray = Rays.rays[direction][square]
            val blockers = ray and occupancy
            if (blockers != 0UL){
                // Zależnie od kierunku szukamy pierwszego blokera skanując od najstarszego bitu
                // lub od najmłodszego bitu.
                val blockerIndex = if (direction == 3 || direction == 5){
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