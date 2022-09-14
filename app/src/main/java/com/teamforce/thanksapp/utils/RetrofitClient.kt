package com.teamforce.thanksapp.utils

import com.teamforce.thanksapp.BuildConfig
import com.teamforce.thanksapp.data.api.ThanksApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    companion object {
        private var instance: ThanksApi? = null
        private val base_url_develop = "http://176.99.6.251:8888"
        private val base_url_product = "http://176.99.6.251:8889"

        fun getInstance(): ThanksApi? {
            if (instance == null) {

                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(logging)

                val retrofit = Retrofit.Builder()
                    .baseUrl(BuildConfig.URL_PORT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build()
                instance =
                    retrofit.create(ThanksApi::class.java)
            }

            return instance
        }
    }
}