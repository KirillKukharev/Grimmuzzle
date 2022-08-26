package com.akvelon.grimmuzzle.data.entities

import androidx.databinding.ObservableBoolean

class Attribute(
    private val id: Int,
    private val imgUrl: String,
    private val fullHDImgUrl: String?,
    private val label: String,
    private val text: String,
    private val type: Type
) {
    val isSelected: ObservableBoolean = ObservableBoolean(false)
    fun getID() = id
    fun getImgURL() = imgUrl
    fun getFullHDImgURL() = fullHDImgUrl
    fun getLabel() = label
    fun getText() = text
    fun getType() = type
    override fun equals(other: Any?): Boolean {
        return other is Attribute &&
                id == other.id &&
                imgUrl == other.imgUrl &&
                fullHDImgUrl == other.fullHDImgUrl &&
                label == other.label &&
                text == other.text &&
                type == other.type
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + imgUrl.hashCode()
        result = 31 * result + fullHDImgUrl.hashCode()
        result = 31 * result + label.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
