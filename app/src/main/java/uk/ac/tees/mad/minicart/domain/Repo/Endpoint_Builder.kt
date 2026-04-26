package uk.ac.tees.mad.minicart.domain.Repo

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uk.ac.tees.mad.minicart.model.Coupon
import uk.ac.tees.mad.minicart.model.productItem

interface EndPoint_Builder {

    @GET("products")
    suspend fun getProducts(): Response<Coupon>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): productItem
}
