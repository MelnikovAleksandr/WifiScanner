package ru.asmelnikov.wifiscanner.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.asmelnikov.wifiscanner.data.WifiDatabase
import ru.asmelnikov.wifiscanner.data.WifiRepositoryImpl
import ru.asmelnikov.wifiscanner.data.WifiScanner
import ru.asmelnikov.wifiscanner.domain.WifiRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideWifiDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            WifiDatabase::class.java,
            "wifi_database"
        ).build()

    @Provides
    @Singleton
    fun provideWifiRepository(db: WifiDatabase): WifiRepository {
        return WifiRepositoryImpl(db.getWifiDao())
    }

    @Provides
    fun provideWifiScanner(@ApplicationContext context: Context): WifiScanner {
        return WifiScanner(context)
    }
}