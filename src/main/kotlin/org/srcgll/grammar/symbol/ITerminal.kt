package org.srcgll.grammar.symbol

interface ITerminal
{
    val value : String

    fun match(pos : Any, str : String) : Boolean
}