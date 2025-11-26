package com.meta.pixelandtexel.scanner.android.views.smarthome

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class LightUiState(
    val isLightOn: Boolean = false,
    val intensity: Float = 0.5f,
    val colorValue: Float = 0.5f
)

class LightViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LightUiState())
    val uiState: StateFlow<LightUiState> = _uiState.asStateFlow()

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
