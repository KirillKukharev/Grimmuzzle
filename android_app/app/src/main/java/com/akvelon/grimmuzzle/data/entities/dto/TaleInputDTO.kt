package com.akvelon.grimmuzzle.data.entities.dto

data class TaleInputDTO(
    val name: String?,
    val length: Int?,
    val input: Map<String, List<Int>>
) {
}
