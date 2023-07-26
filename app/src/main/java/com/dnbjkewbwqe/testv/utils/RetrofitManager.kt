package com.dnbjkewbwqe.testv.utils

import android.webkit.WebSettings
import com.dnbjkewbwqe.testv.application
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.BufferedSink
import okio.GzipSink
import okio.buffer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitManager {
    private const val timeOverSecond = 10L
    private val loggingInterceptor by lazy { HttpLoggingInterceptor{ d(it)}.setLevel(HttpLoggingInterceptor.Level.BODY) }
    private val headerInterceptor by lazy {
        Interceptor{
            val request = it.request().newBuilder()
                .removeHeader("User-Agent")
                .addHeader("User-Agent", WebSettings.getDefaultUserAgent(application))
                .build()
            it.proceed(request)
        }
    }
    private val gzipInterceptor by lazy {
        Interceptor{
            val ordinalRequest = it.request()
            if(ordinalRequest.body == null || ordinalRequest.header("Content-Encoding") != null)
                return@Interceptor it.proceed(ordinalRequest)
            val requestBody = object : RequestBody(){
                override fun contentLength(): Long = -1

                override fun contentType(): MediaType? = ordinalRequest.body?.contentType()

                override fun writeTo(sink: BufferedSink) {
                    val gzipSink = GzipSink(sink).buffer()
                    ordinalRequest.body?.writeTo(gzipSink)
                    gzipSink.close()
                }
            }
            val newRequest = ordinalRequest.newBuilder()
                .method(ordinalRequest.method,requestBody)
                .addHeader("Content-Encoding","gzip")
                .build()
            it.proceed(newRequest)
        }
    }
    private fun getOkhttpClient(gzip : Boolean) : OkHttpClient{
        val okHttpClientBuilder = OkHttpClient.Builder()
            .writeTimeout(timeOverSecond,TimeUnit.SECONDS)
            .readTimeout(timeOverSecond,TimeUnit.SECONDS)
            .connectTimeout(timeOverSecond,TimeUnit.SECONDS)
            .callTimeout(timeOverSecond,TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(headerInterceptor)
        if(gzip)
            okHttpClientBuilder.addInterceptor(gzipInterceptor)
        return okHttpClientBuilder.addInterceptor(loggingInterceptor)
            .build()
    }
    fun getRetrofit(baseUrl : String,gzip : Boolean) =
        Retrofit.Builder()
            .client(getOkhttpClient(gzip))
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

}