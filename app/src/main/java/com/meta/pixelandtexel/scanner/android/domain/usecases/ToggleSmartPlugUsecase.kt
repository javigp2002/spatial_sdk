package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class ToggleSmartPlugUsecase (
    private val repository: SmartHomeRepository
) {
    /**
     * Ejecuta la acción de encender o apagar el enchufe.
     * @param entityId El ID de la entidad del enchufe (ej. "switch.smart_plug_javi").
     * @param turnOn `true` para encender, `false` para apagar.
     * @return `true` si la operación fue exitosa, `false` en caso contrario.
     */
    suspend fun run(entityId: String, turnOn: Boolean): Boolean {
        return if (turnOn) {
            repository.turnOnSwitch(entityId)
        } else {
            repository.turnOffSwitch(entityId)
        }
    }
}
