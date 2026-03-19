package uk.ac.tees.mad.minicart.domain.Repo


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
const val BASE_URL="https://fakestoreapi.com/"
object ApiBuilder {

    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES).build()

    val provedApi = Retrofit.Builder().client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()
        .create(Endpoint_Builder::class.java)
}