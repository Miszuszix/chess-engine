package movegen

/**
 * Generator ataków Gońca w locie (na podstawie zajętości planszy).
 */
object BishopAttacks {

    /**
     * Zwraca maskę bitową ataków Gońca dla danego pola, uwzględniając blokujące figury.
     *
     * @param square Indeks pola, na którym stoi Goniec (0..63)
     * @param occupancy Bitboard reprezentujący wszystkie figury na planszy (zarówno białe jak i czarne)
     */
    fun getAttacks(square: Int, occupancy: ULong): ULong {
        var attacks = 0UL
        for(direction in 1..7 step 2){
            val ray = Rays.rays[direction][square]
            val blockers = ray and occupancy
            if (blockers != 0UL){
                val blockerIndex = if (direction == 3 || direction == 5){
                    63 - blockers.countLeadingZeroBits()
                }else{
                    blockers.countTrailingZeroBits()
                }
                attacks = attacks or (ray xor Rays.rays[direction][blockerIndex])
            }else{
                attacks = attacks or ray
            }
        }
        return attacks
    }
}