package com.meta.pixelandtexel.scanner.android.views.smarthome.plug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetSmartPlugInfoUsecase
import com.meta.pixelandtexel.scanner.android.domain.usecases.ToggleSmartPlugUsecase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SmartPlugViewModel(
    private val toggleSmartPlugUsecase: ToggleSmartPlugUsecase,
    private val getSmartPlugInfoUsecase: GetSmartPlugInfoUsecase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SmartPlugUiState())
    val uiState: StateFlow<SmartPlugUiState> = _uiState.asStateFlow()

    private var pollingJob: Job? = null

    private var entityId: String? = null

    fun initialize(entityId: String) {
        this.entityId = entityId
        startPolling()
    }

    /**
     * Inicia un bucle que consulta el estado del enchufe cada 2 segundos.
     */
    private fun startPolling() {
        pollingJob?.cancel() // Cancela cualquier bucle anterior
//        pollingJob = viewModelScope.launch {
//            while (true) {
//                refreshPlugInfo()
//                delay(2000)
//            }
//        }
    }

    /**
     * Solicita la información más reciente del enchufe y actualiza el UiState.
     */
    private suspend fun refreshPlugInfo() {
        entityId?.let { id ->
            val plugInfo = getSmartPlugInfoUsecase.run("")
            if (plugInfo != null) {
                _uiState.update {
                    it.copy(
                        isPlugOn = plugInfo.isPlugOn,
                        consumptionW = plugInfo.consumptionW,
                        currentA = plugInfo.currentA,
                        voltageV = plugInfo.voltageV
                    )
                }
            }
        }
    }

    /**
     * Cambia el estado del enchufe (encendido/apagado).
     */
    fun togglePlug() {
        entityId?.let { id ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val currentStatus = _uiState.value.isPlugOn
                val success = toggleSmartPlugUsecase.run(entityId =
                    id,
                    turnOn = !currentStatus)

                if (success) {
                    _uiState.update { it.copy(isPlugOn = !currentStatus) }
                }

                refreshPlugInfo()

                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
