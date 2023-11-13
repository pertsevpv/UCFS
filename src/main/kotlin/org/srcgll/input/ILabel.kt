package org.srcgll.input

import org.srcgll.grammar.symbol.Terminal

interface ILabel
{
    val terminal : Terminal<*>?

    override fun equals(other : Any?) : Boolean
}