package org.kotgll.rsm.grammar.symbol

open class Terminal<T>(val value: T) : Symbol {

    override fun toString() = "Terminal(${value.toString()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Terminal<*>) return false
        return value == other.value
    }

    val hashCode: Int = value.hashCode()
    override fun hashCode() = hashCode
}

//data class Terminal(override val value: String) : Terminal<String>(value) {
//    val size: Int = value.length
//
//    fun match(pos: Int, input: String) = input.startsWith(value, pos)
//
//    override fun toString() = "Literal($value)"
//}
