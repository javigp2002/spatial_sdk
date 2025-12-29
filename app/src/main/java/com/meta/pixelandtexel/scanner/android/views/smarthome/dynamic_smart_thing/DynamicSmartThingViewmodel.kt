package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.EntityUiModel
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.SmartDeviceUiState
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DynamicSmartThingViewmodel() : ViewModel() {
    private val _uiState = MutableStateFlow(SmartDeviceUiState())
    val uiState: StateFlow<SmartDeviceUiState> = _uiState.asStateFlow()

    fun initialize(device: Device) {
        _uiState.update {
            SmartDeviceUiState(
                deviceName = device.name,
                entities = device.entityList.sortedBy { entity -> entity.domain.toString() }
                    .map { entity ->
                        EntityUiModel(
                            id = entity.id,
                            name = entity.id.split(".").last().replace("_", " ").capitalize(),
                            domain = entity.domain
                        )
                    }
            )
        }
        // start polling for real time vals

    }

    fun onSwitchToggled(entityId: String, newValue: Boolean) {
        viewModelScope.launch {
            updateEntityState(entityId) { it.copy(isUpdating = true) }

            // usecase

            delay(500)
            val success = true

            if (success) {
                updateEntityState(entityId) { currentEntity ->
                    val currentDomain = currentEntity.domain
                    if (currentDomain is SwitchDomain) {
                        currentEntity.copy(
                            domain = currentDomain.copy(value = newValue),
                            isUpdating = false
                        )
                    } else {
                        currentEntity.copy(isUpdating = false)
                    }
                }
            } else {
                updateEntityState(entityId) { it.copy(isUpdating = false) }
            }
        }
    }

    private fun updateEntityState(entityId: String, update: (EntityUiModel) -> EntityUiModel) {
        _uiState.update { currentState ->
            currentState.copy(
                entities = currentState.entities.map { entity ->
                    if (entity.id == entityId) update(entity) else entity
                }
            )
        }
    }

    private fun String.capitalize() =
        replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}