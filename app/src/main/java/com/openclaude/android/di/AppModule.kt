package com.openclaude.android.di

import android.content.Context
import com.openclaude.android.data.remote.ApiService
import com.openclaude.android.data.remote.FileApiService
import com.openclaude.android.data.remote.GitApiService
import com.openclaude.android.data.remote.McpApiService
import com.openclaude.android.data.remote.TerminalApiService
import com.openclaude.android.data.remote.ToolApiService
import com.openclaude.android.data.remote.GitHubApiService
import com.openclaude.android.data.remote.StreamingClient
import com.openclaude.android.data.repository.SettingsRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFileApiService(retrofit: Retrofit): FileApiService {
        return retrofit.create(FileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideTerminalApiService(retrofit: Retrofit): TerminalApiService {
        return retrofit.create(TerminalApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideToolApiService(retrofit: Retrofit): ToolApiService {
        return retrofit.create(ToolApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMcpApiService(retrofit: Retrofit): McpApiService {
        return retrofit.create(McpApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitHubApiService(retrofit: Retrofit): GitHubApiService {
        return retrofit.create(GitHubApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGitApiService(retrofit: Retrofit): GitApiService {
        return retrofit.create(GitApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideStreamingClient(moshi: Moshi): StreamingClient {
        return StreamingClient(moshi)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepository(context)
    }
}
