package org.srcgll.dsl

import org.srcgll.grammar.RSMState
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMemberProperties


object GlobalState {
    private var value = 0
    fun getNextInt(): Int = value++
}

interface JFlexConvertable {
    fun getJFlex(name: String?): String
}

open class Grammar : JFlexConvertable {
    val nonTerms = ArrayList<NT>()
    private lateinit var startState: NT
    fun setStart(state: Regexp) {
        if (state is NT) {
            startState = state
        }
    }

    fun getMembers(): Collection<KProperty1<Grammar, *>> {
        val thisClass = this::class as KClass<Grammar>
        return thisClass.declaredMemberProperties
    }

    private fun <T : Any> getMembers(neededClass: KClass<T>): Sequence<KProperty1<Grammar, *>> {
        val thisClass = this::class as KClass<Grammar>
        return thisClass.declaredMemberProperties.asSequence()
            .filter { it.returnType.classifier == neededClass }
    }

    override fun getJFlex(name: String?): String =
        GrammarConverter(this).generateLexerText()

    fun toRsm(): RSMState {
        nonTerms.forEach { it.buildRsmBox() }
        return startState.nonTerm.startState
    }
}

class GrammarConverter(private val gr: Grammar) {
    private val prefix =
        """package org.srcgll.lexer;

import java.io.*;
import org.srcgll.lexer.Token;
import org.srcgll.lexer.SymbolCode;
%%

%public
%class GeneratedLexer
%type Token
%unicode

%{
    public Token token(SymbolCode tokenType)
    {
        return new Token<SymbolCode>(tokenType, yytext());
    }
%}
"""
    private val postfix = "<<EOF>>     { return token(SymbolCode.EOF); }"
    private val separator = "%%"
    private val enumNames = HashSet<String>()
    private fun formatLiteralRule(literalToken: KProperty1<Grammar, *>): String? {
        if (literalToken.returnType.classifier != Term::class)
            return null
        return (literalToken.get(gr) as Term).getJFlex(literalToken.name.uppercase())
    }

    private fun formatRegexRule(regexToken: KProperty1<Grammar, *>): String? {
        return if (regexToken.returnType.classifier == RegexpTerm::class)
            (regexToken.get(gr) as RegexpTerm).getJFlex(regexToken.name)
        else null
    }

    private fun formatRegexDeclaration(regexToken: KProperty1<Grammar, *>): String? {
        return if (regexToken.returnType.classifier == RegexpTerm::class)
            (regexToken.get(gr) as RegexpTerm).getJFlexDeclaration(regexToken.name)
        else null
    }

    fun generateLexerText(): String {
        val members = gr.getMembers()
        val regexpDeclarations = members.mapNotNull { formatRegexDeclaration(it) }.joinToString(separator = "\n")
        val literalTokens = members.mapNotNull { formatLiteralRule(it) }.joinToString(separator = "\n")
        val regexpTokens = members.mapNotNull { formatRegexRule(it) }.joinToString(separator = "\n")
        val code = listOf(prefix, regexpDeclarations, separator, literalTokens, regexpTokens, postfix)
        return code.joinToString(separator = "\n")
    }
}

