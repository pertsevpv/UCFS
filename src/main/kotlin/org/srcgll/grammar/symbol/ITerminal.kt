package org.srcgll.grammar.symbol

interface ITerminal
{
    val value : String

    override fun hashCode() : Int
    override fun equals(other : Any?) : Boolean
}