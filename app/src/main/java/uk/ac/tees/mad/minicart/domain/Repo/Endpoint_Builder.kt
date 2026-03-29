package uk.ac.tees.mad.minicart.domain.Repo

import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Path
import uk.ac.tees.mad.minicart.model.product
import uk.ac.tees.mad.minicart.model.productItem

interface Endpoint_Builder {
@GET("products")
suspend fun getProducts(): List<productItem>

    @GET("products/{id}")
    suspend fun getProductById(
        @Path("id") id: Int
    ): productItem

}
