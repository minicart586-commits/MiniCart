package uk.ac.tees.mad.minicart.domain.Repo


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
const val BASE_URL = "https://dummyjson.com/"
object ApiBuilder {

    val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.MINUTES)
        .build()

    val provedApi: EndPoint_Builder = Retrofit.Builder()
        .client(client)
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(EndPoint_Builder::class.java)
}
