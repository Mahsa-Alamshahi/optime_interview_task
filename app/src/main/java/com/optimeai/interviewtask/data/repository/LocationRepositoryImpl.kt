package com.optimeai.interviewtask.data.repository

import com.optimeai.interviewtask.data.data_source.LocationDataSource
import com.optimeai.interviewtask.domain.dto.LocationDetails
import com.optimeai.interviewtask.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(private val locationDataSource: LocationDataSource) :
    LocationRepository {

    override fun getLocationUpdates(): Flow<LocationDetails> = locationDataSource.getUserLocation()


}