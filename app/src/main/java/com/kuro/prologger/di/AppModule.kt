package com.kuro.prologger.di

import com.kuro.prologger.navigation.util.AppNavigator
import com.kuro.prologger.navigation.util.AppNavigatorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppNavigator(): AppNavigator = AppNavigatorImpl()

}