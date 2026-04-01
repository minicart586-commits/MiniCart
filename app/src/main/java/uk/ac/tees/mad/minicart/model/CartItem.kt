package uk.ac.tees.mad.minicart.model

data class CartItem(
    val product: productItem,
    val quantity: Int = 1
)
