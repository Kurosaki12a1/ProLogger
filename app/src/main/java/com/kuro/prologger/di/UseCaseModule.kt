package com.kuro.prologger.di

import com.kuro.prologger.domain.repository.PermissionRepository
import com.kuro.prologger.domain.usecase.ForegroundServiceUseCase
import com.kuro.prologger.domain.usecase.OverlayPermissionUseCase
import com.kuro.prologger.domain.usecase.StoragePermissionUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideStoragePermissionUseCase(
        repository: PermissionRepository
    ): StoragePermissionUseCase = StoragePermissionUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideOverlayPermissionUseCase(
        repository: PermissionRepository
    ): OverlayPermissionUseCase = OverlayPermissionUseCase(repository)

    @Provides
    @ViewModelScoped
    fun provideServicePermissionUseCase(
        repository: PermissionRepository
    ): ForegroundServiceUseCase = ForegroundServiceUseCase(repository)
}