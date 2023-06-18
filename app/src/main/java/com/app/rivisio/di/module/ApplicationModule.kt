package com.app.rivisio.di.module

import android.content.Context
import com.app.rivisio.BuildConfig
import com.app.rivisio.data.db.AppDatabase
import com.app.rivisio.data.db.DbHelper
import com.app.rivisio.data.db.DbHelperImpl
import com.app.rivisio.data.network.ApiHelper
import com.app.rivisio.data.network.ApiHelperImpl
import com.app.rivisio.data.network.ApiService
import com.app.rivisio.data.prefs.PreferencesHelper
import com.app.rivisio.data.prefs.PreferencesHelperImpl
import com.app.rivisio.data.repository.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Provides
    @Singleton
    internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        return if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            }
        } else {
            httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    @Provides
    @Singleton
    internal fun provideDisableCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("cache-control", "no-cache")
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        disableCacheInterceptor: Interceptor
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .readTimeout(10 * 1000, TimeUnit.MILLISECONDS)
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(disableCacheInterceptor)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(
        BASE_URL: String,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    internal fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper

    @Provides
    @Singleton
    fun providePreferenceHelper(preferencesHelper: PreferencesHelperImpl): PreferencesHelper =
        preferencesHelper

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)!!
    }

    @Provides
    @Singleton
    fun provideDbHelper(dbHelper: DbHelperImpl): DbHelper = dbHelper

    @Provides
    @Singleton
    fun provideRepository(
        apiHelper: ApiHelper,
        preferencesHelper: PreferencesHelper,
        dbHelper: DbHelper
    ): Repository {
        return Repository(apiHelper, preferencesHelper, dbHelper)
    }
}