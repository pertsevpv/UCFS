package org.srcgll.input

import org.srcgll.grammar.symbol.Terminal

interface ILabel <TerminalType>
{
    val terminal : Terminal<TerminalType>

    override fun equals(other : Any?) : Boolean
}