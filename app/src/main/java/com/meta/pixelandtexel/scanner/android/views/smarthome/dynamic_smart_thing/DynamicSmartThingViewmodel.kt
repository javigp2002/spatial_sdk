package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meta.pixelandtexel.scanner.android.domain.usecases.GetDeviceInfoUsecase
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.EntityUiModel
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.SmartDeviceUiState
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.ThingEntity
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DynamicSmartThingViewmodel(
    private val getDeviceInfoUsecase: GetDeviceInfoUsecase
) : ViewModel() {
    companion object {
        private const val WAIT_FOR_NEXT_REQUEST_MS = 5000L
    }
    private val _uiState = MutableStateFlow(SmartDeviceUiState())
    val uiState: StateFlow<SmartDeviceUiState> = _uiState

    fun initialize(device: Device) {
        val newState = SmartDeviceUiState(
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

        _uiState.value = newState

        viewModelScope.launch {
            while (true) {
                val updatedEntities = getDeviceInfoUsecase.run(_uiState.value.entities.map {
                    ThingEntity(
                        id = it.id,
                        domain = it.domain
                    )
                })

                if (updatedEntities.isEmpty()) {
                    delay(WAIT_FOR_NEXT_REQUEST_MS)
                    continue
                }

                _uiState.update { currentState ->
                    currentState.copy(
                        entities = updatedEntities.map { entity ->
                            EntityUiModel(
                                id = entity.id,
                                name = entity.id.split(".").last().replace("_", " ").capitalize(),
                                domain = entity.domain
                            )
                        }
                    )
                }
                delay(WAIT_FOR_NEXT_REQUEST_MS)
            }
        }

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