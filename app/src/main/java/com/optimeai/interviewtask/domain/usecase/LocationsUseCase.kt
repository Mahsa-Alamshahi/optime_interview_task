package com.optimeai.interviewtask.domain.usecase

import com.optimeai.interviewtask.domain.dto.LocationDetails
import com.optimeai.interviewtask.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocationsUseCase @Inject constructor(private val locationRepository: LocationRepository) {

//    @SuppressLint("SuspiciousIndentation")
    operator fun invoke(): Flow<LocationDetails> = locationRepository.getLocationUpdates()


}