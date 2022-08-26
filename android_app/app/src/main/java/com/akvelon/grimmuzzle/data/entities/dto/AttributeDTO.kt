package com.akvelon.grimmuzzle.data.entities.dto

import com.google.gson.annotations.SerializedName

class AttributeDTO(
    @SerializedName("id")
    private val id: Int,
    @SerializedName("img_url")
    private val imgURL: String,
    @SerializedName("full_hd_img_url")
    private val fullHDImgURL: String,
    @SerializedName("label")
    private val label: String,
    @SerializedName("text")
    private val text: String
) {
    fun getID() = id
    fun getImgURL() = imgURL
    fun getFullHDImgURL() = fullHDImgURL
    fun getLabel() = label
    fun getText() = text
}
