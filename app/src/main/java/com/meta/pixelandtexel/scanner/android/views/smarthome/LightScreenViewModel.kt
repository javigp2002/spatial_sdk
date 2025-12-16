package com.meta.pixelandtexel.scanner.android.views.smarthome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetConnectionUsecase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LightUiState(
    val isConnected: Boolean = false,
    val isLightOn: Boolean = false,
    val intensity: Float = 0.5f,
    val colorValue: Float = 0.5f
)

class LightViewModel(
    private val getConnectionUsecase: GetConnectionUsecase
):  ViewModel() {

    private val _uiState = MutableStateFlow(LightUiState())
    val uiState: StateFlow<LightUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch{
            val connected = getConnectionUsecase.run(true)
            _uiState.update { it.copy(isConnected = connected) }
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
