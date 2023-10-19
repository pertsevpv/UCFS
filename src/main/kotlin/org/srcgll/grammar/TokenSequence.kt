package org.srcgll.grammar

import org.srcgll.grammar.symbol.Terminal
import java.util.HashMap

class TokenId private constructor()
{
    companion object {
        private var curTokenId : Int = 0

        fun getFirstFreeTokenId() = curTokenId++
    }
}

interface TokenSequence
{
    val nextToken  : HashMap<Terminal, TokenSequence>
    val isStart    : Boolean
    val isFinal    : Boolean
}

class LinearInput
(
    override val isStart : Boolean = false,
    override val isFinal : Boolean = false,
    val id : Int = TokenId.getFirstFreeTokenId(),
)
    : TokenSequence
{
    override val nextToken : HashMap<Terminal, TokenSequence> = HashMap()

    override fun toString() : String {
        return "Token($id)"
    }
}