package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.model.SmartPlugInfo
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class GetSmartPlugInfoUsecase (
    private val repository: SmartHomeRepository
) {
    /**
     * Obtiene la información más reciente del estado del enchufe inteligente.
     * @param entityId El ID de la entidad del enchufe.
     * @return Un objeto [SmartPlugInfo] con los datos, o null si falla.
     */
    suspend fun run(entityId: String): SmartPlugInfo? {
        return repository.getSmartPlugInfo(entityId)
    }
}
