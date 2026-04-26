package uk.ac.tees.mad.minicart.model

import com.google.gson.annotations.SerializedName

data class productItem(
    val category: String,
    val description: String,
    val id: Int,
    @SerializedName("thumbnail")
    val image: String,
    val price: Double,
    val title: String
)