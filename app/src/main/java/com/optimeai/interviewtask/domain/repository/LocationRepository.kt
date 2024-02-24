package com.optimeai.interviewtask.domain.repository

import com.optimeai.interviewtask.domain.dto.LocationDetails
import kotlinx.coroutines.flow.Flow


interface LocationRepository {

    fun getLocationUpdates(): Flow<LocationDetails>
}