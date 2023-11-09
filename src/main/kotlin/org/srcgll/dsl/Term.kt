package org.srcgll.dsl

import org.srcgll.grammar.symbol.Terminal

sealed class Symbol : Regexp() {
    override fun derive(symbol: Symbol): Regexp {
        return if (this == symbol) Epsilon else Empty
    }
}

open class Term(val text: String) : Symbol(), JFlexConvertable {
    override fun getJFlex(name: String?): String {
        return "\"${text}\"\t\t{ return token(SymbolCode.${name}); }"
    }

    val terminal: Terminal = Terminal(text)
}

class RegexpTerm(text: String, private val ignore: Boolean = false) : Term(text), JFlexConvertable {
    override fun getJFlex(name: String?): String {
        return if (ignore) "{${name}}\t\t{}" else "{${name}}\t\t{ return token(SymbolCode.${name?.uppercase()}); }"
    }

    fun getJFlexDeclaration(name: String): String = "${name}\t\t=\t${text}"
}