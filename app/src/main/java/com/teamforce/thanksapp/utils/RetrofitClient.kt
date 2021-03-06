package com.teamforce.thanksapp.utils

import com.teamforce.thanksapp.data.api.ThanksApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {

    companion object {
        private var instance: ThanksApi? = null

        fun getInstance(): ThanksApi? {
            if (instance == null) {

                val logging = HttpLoggingInterceptor()
                logging.level = HttpLoggingInterceptor.Level.BODY
                val httpClient = OkHttpClient.Builder()
                httpClient.addInterceptor(logging)

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://176.99.6.251:8888")
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
