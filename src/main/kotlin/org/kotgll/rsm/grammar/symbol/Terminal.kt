package org.kotgll.rsm.grammar.symbol

class Terminal(val value : String) : Symbol
{
    val size : Int = value.length
    fun match(pos : Int, input : String) = input.startsWith(value, pos)

    override fun toString() = "Literal($value)"

    override fun equals(other : Any?) : Boolean
    {
        if (other !is Terminal) return false

        if (this === other)     return true

        return value == other.value
    }

    val hashCode: Int = value.hashCode()
    override fun hashCode() = hashCode
}
