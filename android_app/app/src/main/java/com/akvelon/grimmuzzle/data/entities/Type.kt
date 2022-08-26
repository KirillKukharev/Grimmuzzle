package com.akvelon.grimmuzzle.data.entities

class Type(private val multiple: Boolean, private val label: String, private val tabIndex: Int) {
    fun isMultiple() = multiple
    fun getLabel() = label
    fun getTabIndex() = tabIndex
    override fun equals(other: Any?): Boolean {
        return other is Type &&
                multiple == other.multiple &&
                label == other.label
    }

    override fun hashCode(): Int {
        var result = multiple.hashCode()
        result = 31 * result + label.hashCode()
        return result
    }
}