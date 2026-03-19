package uk.ac.tees.mad.minicart.model

data class productItem(
    val category: String,
    val description: String,
    val id: Int,
    val image: String,
    val price: Double,
    val title: String
)