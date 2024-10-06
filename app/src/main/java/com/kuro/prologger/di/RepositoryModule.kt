package com.kuro.prologger.di

import android.content.Context
import com.kuro.prologger.data.repository.PermissionRepositoryImpl
import com.kuro.prologger.domain.repository.PermissionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePermissionRepository(
        @ApplicationContext context: Context
    ): PermissionRepository = PermissionRepositoryImpl(context)

}