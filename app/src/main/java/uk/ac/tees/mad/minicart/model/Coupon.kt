package uk.ac.tees.mad.minicart.model

data class Coupon(
    val limit: Int,
    val products: List<productItem>,
    val skip: Int,
    val total: Int
)
