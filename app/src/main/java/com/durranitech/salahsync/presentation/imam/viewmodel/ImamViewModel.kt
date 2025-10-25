package com.durranitech.salahsync.presentation.imam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.durranitech.salahsync.domain.model.Masjid
import com.durranitech.salahsync.domain.usecase.CreateMasjidUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidDetailsUseCase
import com.durranitech.salahsync.domain.usecase.GetMasjidUseCase
import com.durranitech.salahsync.presentation.imam.ImamIntent
import com.durranitech.salahsync.presentation.imam.ImamUiState
import com.durranitech.salahsync.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ImamViewModel @Inject constructor(
    private val createMasjidUseCase: CreateMasjidUseCase,
    private val getMasjidUseCase: GetMasjidUseCase,
    private val getMasjidDetailsUseCase: GetMasjidDetailsUseCase
): ViewModel(){

    private val _uiState = MutableStateFlow(ImamUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        getMasjid()
        getMasjidDetails()
    }

    fun onImamEvent(intent: ImamIntent){
        when(intent){
            ImamIntent.ClearMessage -> TODO()
            ImamIntent.LoadSalahTimes -> TODO()
            is ImamIntent.UpdateSalahTimes -> TODO()
            is ImamIntent.createMasjid -> createMasjid(intent.masjid)
            is ImamIntent.updateMasjid -> TODO()
            ImamIntent.getMasjid -> getMasjid()
        }

    }

    private fun createMasjid(masjid: Masjid) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = createMasjidUseCase(masjid)

            when (result) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        successMessage = "Masjid created successfully ✅",
                        masjid = masjid
                    )
                }

                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                is Resource.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }

    fun getMasjid(){
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = getMasjidUseCase()
            when(result){
                is Resource.Error -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Masjid fetched successfully ✅"
                )
                is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                is Resource.Success -> _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = null,
                    masjid = result.data,
                    successMessage = "Masjid fetched successfully ✅"
                )
            }
        }
    }
    fun getMasjidDetails() {
        viewModelScope.launch {
            getMasjidDetailsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.value = _uiState.value.copy(isLoading = true)
                    is Resource.Success -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        masjid = result.data,
                        successMessage = "Masjid details loaded ✅"
                    )
                    is Resource.Error -> _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }


}