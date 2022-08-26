package com.akvelon.grimmuzzle.data.entities.dto

import com.akvelon.grimmuzzle.data.entities.Attribute
import com.akvelon.grimmuzzle.data.entities.Type
import com.google.gson.annotations.SerializedName

class AttributesByTypeDTO(
    @SerializedName("multiple")
    private val multiple: Boolean,
    @SerializedName("items")
    val attributes: List<AttributeDTO>,
    @SerializedName("tabIndex")
    private val tabIndex: Int
) {
    fun convertFromDTO(typeName: String): MutableList<Attribute> {
        val builtAttributes: MutableList<Attribute> = mutableListOf()
        attributes.forEach {
            builtAttributes.add(
                Attribute(
                    it.getID(),
                    it.getImgURL(),
                    it.getFullHDImgURL(),
                    it.getLabel(),
                    it.getText(),
                    Type(this.multiple, typeName, tabIndex)
                )
            )
        }
        return builtAttributes
    }
}