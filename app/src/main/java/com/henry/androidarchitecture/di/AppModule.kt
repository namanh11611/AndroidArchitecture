package com.henry.androidarchitecture.di

import android.content.Context
import androidx.room.Room
import com.henry.androidarchitecture.data.local.AppDatabase
import com.henry.androidarchitecture.data.local.NoteDao
import com.henry.androidarchitecture.data.repository.NoteRepository
import com.henry.androidarchitecture.data.repository.NoteRepositoryImpl
import com.henry.androidarchitecture.util.AppUtils
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherDefault

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DispatcherIO

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppUtils.DATABASE_NAME
        ).build()

    @Singleton
    @Provides
    fun providesNoteDao(appDatabase: AppDatabase): NoteDao = appDatabase.noteDao()

    @DispatcherDefault
    @Singleton
    @Provides
    fun providesDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @DispatcherIO
    @Singleton
    @Provides
    fun providesDispatcherIO(): CoroutineDispatcher = Dispatchers.IO
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Singleton
    @Binds
    abstract fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository
}
