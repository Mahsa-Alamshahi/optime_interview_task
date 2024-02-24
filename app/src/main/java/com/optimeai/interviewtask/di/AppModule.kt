package com.optimeai.interviewtask.di

import android.content.Context
import com.optimeai.interviewtask.data.data_source.LocationDataSource
import com.optimeai.interviewtask.data.repository.LocationRepositoryImpl
import com.optimeai.interviewtask.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideAppContext(@ApplicationContext context: Context): Context = context


    @Provides
    @Singleton
    fun provideLocationRepository(locationDataSource: LocationDataSource): LocationRepository =
        LocationRepositoryImpl(locationDataSource = locationDataSource)
}