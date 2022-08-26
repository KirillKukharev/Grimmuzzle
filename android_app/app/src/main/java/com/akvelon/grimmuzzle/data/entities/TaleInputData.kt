package com.akvelon.grimmuzzle.data.entities

import com.google.gson.annotations.SerializedName


data class TaleInputData(
    @SerializedName("attributes")
    val inputMap: MutableMap<String, List<Int>>,
    @SerializedName("generatedString")
    val generatedString: String
) {
    override fun equals(other: Any?): Boolean {
        return other is TaleInputData &&
                inputMap == other.inputMap &&
                generatedString == other.generatedString
    }

    override fun hashCode(): Int = inputMap.hashCode() * 31 + generatedString.hashCode()
}
