package org.srcgll.grammar.symbol

class Terminal
(
    override val value : String
)
    : ITerminal
{
    override fun toString() = "Literal($value)"

    override fun equals(other : Any?) : Boolean
    {
        if (this === other)       return true
        if (other !is Terminal)   return false
        if (value != other.value) return false

        return true
    }

    val hashCode : Int = value.hashCode()
    override fun hashCode() = hashCode
}
