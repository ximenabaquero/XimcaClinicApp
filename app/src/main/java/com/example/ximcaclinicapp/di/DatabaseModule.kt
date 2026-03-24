package com.example.ximcaclinicapp.di

import android.content.Context
import androidx.room.Room
import com.example.ximcaclinicapp.data.AppDatabase
import com.example.ximcaclinicapp.data.PacienteDao
import com.example.ximcaclinicapp.data.PacienteRepository
import com.example.ximcaclinicapp.data.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "ximca_database"
        ).build()
    }

    @Provides
    fun providePacienteDao(database: AppDatabase): PacienteDao {
        return database.pacienteDao()
    }

    @Provides
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao {
        return database.usuarioDao()
    }

    @Provides
    @Singleton
    fun providePacienteRepository(pacienteDao: PacienteDao): PacienteRepository {
        return PacienteRepository(pacienteDao)
    }
}