package board

/**
 * Obiekt odpowiedzialny za kodowanie i dekodowanie ruchów
 * z 32-bitowej liczby całkowitej (Int).
 */
object Move {

    fun encode(
        sourceSquare: Int,
        targetSquare: Int,
        piece: Int,
        promotedPiece: Int = 0,
        isCapture: Boolean = false,
        isDoublePawnPush: Boolean = false,
        isEnPassant: Boolean = false,
        isCastling: Boolean = false
    ): Int {
        val castling = if (isCastling) 1 else 0
        val enPassant = if (isEnPassant) 1 else 0
        val doublePawnPush = if (isDoublePawnPush) 1 else 0
        val capture = if (isCapture) 1 else 0
        var result: Int = castling
        result = result shl 1 or enPassant
        result = result shl 1 or doublePawnPush
        result = result shl 1 or capture
        result = result shl 3 or promotedPiece
        result = result shl 3 or piece
        result = result shl 6 or targetSquare
        result = result shl 6 or sourceSquare
        return result
    }

    fun getSourceSquare(move: Int): Int {
        return move and 0x3F
    }

    fun getTargetSquare(move: Int): Int {
        return (move shr 6) and 0x3F
    }

    fun getPiece(move: Int): Int {
        return (move shr 12) and 0x7
    }

    fun getPromotedPiece(move: Int): Int {
        return (move shr 15) and 0x7
    }

    fun isCapture(move: Int): Boolean {
        return (move shr 18) and 1 == 1
    }

    fun isDoublePawnPush(move: Int): Boolean {
        return (move shr 19) and 1 == 1
    }

    fun isEnPassant(move: Int): Boolean {
        return (move shr 20) and 1 == 1
    }

    fun isCastling(move: Int): Boolean {
        return (move shr 21) and 1 == 1
    }
}