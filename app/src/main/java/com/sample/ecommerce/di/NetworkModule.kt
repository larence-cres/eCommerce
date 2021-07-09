package com.sample.ecommerce.di

import android.app.Activity
import com.sample.ecommerce.data.AppConstants
import com.sample.ecommerce.data.DataStorePreference
import com.sample.ecommerce.data.api.ApiUrls
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.features.*
import java.util.concurrent.TimeUnit
import io.ktor.client.features.json.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import kotlinx.coroutines.flow.first
import okhttp3.ConnectionPool
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.koinApplication
import timber.log.Timber

class NetworkModule {
    val client = HttpClient(OkHttp) {
        defaultRequest {
            host = ApiUrls.BASE_URL
            url { protocol = URLProtocol.HTTP }
        }
        install(DefaultRequest) {
            headers.append(HttpHeaders.Accept, ContentType.Application.Json)
            headers.append(HttpHeaders.ContentType, ContentType.Application.Json)
            headers.append(
                "Authorization",
                "Bearer ${AppConstants.TOKEN.replace("^\"|\"$".toRegex(), "")}"
            )
        }
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
        engine {
            config {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                callTimeout(60, TimeUnit.SECONDS)
                connectTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
            }
            clientCacheSize = 0
            config {
                connectionPool(ConnectionPool(5, 10, TimeUnit.SECONDS))
            }
        }
    }
}