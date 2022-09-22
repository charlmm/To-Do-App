package com.charles.mvvmto_doapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.charles.mvvmto_doapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context,
        callback: TaskDatabase.CallBack
    ) =
        Room.databaseBuilder(app, TaskDatabase::class.java, "taskdb")
            .fallbackToDestructiveMigrationFrom()
            .addCallback(callback)
            .build()
    @Provides
    @Singleton
    fun providesTaskDao(db: TaskDatabase) = db.taskDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope