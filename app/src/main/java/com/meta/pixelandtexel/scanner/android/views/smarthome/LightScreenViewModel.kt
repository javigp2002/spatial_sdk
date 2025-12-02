package com.meta.pixelandtexel.scanner.android.views.smarthome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetConnectionUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LightUiState(
    val isConnected: Boolean = false,
    val isLightOn: Boolean = false,
    val intensity: Float = 0.5f,
    val colorValue: Float = 0.5f
)

@HiltViewModel
class LightViewModel @Inject constructor(
    private val getConnectionUseCase: GetConnectionUsecase
) :  ViewModel() {

    private val _uiState = MutableStateFlow(LightUiState())
    val uiState: StateFlow<LightUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch{
            val connectionStatus = getConnectionUseCase.run()
            _uiState.update { it.copy(isConnected = connectionStatus) }
        }
    }

    fun toggleLight(isOn: Boolean) {
        _uiState.update { it.copy(isLightOn = isOn) }
    }

    fun updateIntensity(newIntensity: Float) {
        _uiState.update { it.copy(intensity = newIntensity) }
    }

    fun updateColor(newColor: Float) {
        _uiState.update { it.copy(colorValue = newColor) }
    }
}
