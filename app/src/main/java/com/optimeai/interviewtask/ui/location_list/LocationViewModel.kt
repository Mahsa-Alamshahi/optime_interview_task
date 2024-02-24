package com.optimeai.interviewtask.ui.location_list

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.optimeai.interviewtask.domain.dto.LocationDetails
import com.optimeai.interviewtask.domain.usecase.LocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationsUseCase: LocationsUseCase
) : ViewModel() {


    var locationListState = mutableStateListOf<LocationDetails>()


    fun monitorUserLocation() {
        viewModelScope.launch {
            locationsUseCase().collect { locationDetails ->
                    locationListState.add(locationDetails)
                }
        }
    }
}