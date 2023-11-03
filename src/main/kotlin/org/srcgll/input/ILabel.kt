package org.srcgll.input

import org.srcgll.grammar.symbol.ITerminal

interface ILabel
{
    val terminal : ITerminal

    override fun equals(other : Any?) : Boolean
}