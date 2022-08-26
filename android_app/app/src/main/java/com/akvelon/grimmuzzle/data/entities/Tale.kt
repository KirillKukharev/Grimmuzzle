package com.akvelon.grimmuzzle.data.entities

import com.google.gson.annotations.SerializedName

data class Tale(
    val id: String,
    @SerializedName("name")
    var title: String?,
    @SerializedName("text")
    val content: String?,
    @SerializedName("length")
    val lengthInWords: Int?,
    val timestamp: String?,
    @SerializedName("input")
    val input: TaleInputData,
    @SerializedName("instore")
    val inStore: Boolean
) {
    override fun equals(other: Any?): Boolean {
        return other is Tale &&
                id == other.id &&
                title == other.title &&
                content == other.content &&
                lengthInWords == other.lengthInWords &&
                timestamp == other.timestamp &&
                input == other.input &&
                inStore == other.inStore
    }

    fun update(other: Tale) {
        title = other.title
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + lengthInWords.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + inStore.hashCode()
        return result
    }
}