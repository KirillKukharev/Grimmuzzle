package com.akvelon.grimmuzzle.data.api

import com.akvelon.grimmuzzle.data.entities.Tale
import com.akvelon.grimmuzzle.data.entities.dto.AttributesByTypeDTO
import com.akvelon.grimmuzzle.data.entities.dto.TaleForRenamingDTO
import com.akvelon.grimmuzzle.data.entities.dto.TaleInputDTO
import retrofit2.Call
import retrofit2.http.*

interface GrimmuzzleAPI {

    @GET("taleConstructorParams")
    fun getAllAttributes(@Header("Cache-Control") header: String = "no-cache"): Call<Map<String, AttributesByTypeDTO>>

    @PATCH("Fairytale")
    fun renameTale(
        @Body obj: TaleForRenamingDTO
    ): Call<Void>

    @POST("Fairytale")
    fun createTale(
        @Body obj: TaleInputDTO
    ): Call<Tale>

    @GET("Fairytale/{id}")
    fun getTaleById(
        @Header("Cache-Control") header: String = "no-cache",
        @Path("id") id: String
    ): Call<Tale>

    @POST("FairyTale/AddToStore/{id}")
    fun shareTaleById(
        @Path("id") id: String
    ): Call<Void>

    @GET("FairyTale/GetTalesFromStore")
    fun getSharedTalesPreviewByRange(
        @Header("Cache-Control") header: String = "no-cache",
        @Query("startIndex") startIndex: Int,
        @Query("quantity") quantity: Int
    ): Call<List<Tale>>
}