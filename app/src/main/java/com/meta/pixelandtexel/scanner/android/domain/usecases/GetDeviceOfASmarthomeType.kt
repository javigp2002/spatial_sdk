package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.models.smarthomedata.TypeSmartHomeInfo

class GetDevicesOfASmarthomeType(
    private val repository: SmartHomeRepository
) {
    suspend fun run(type: TypeSmartHomeInfo): List<String> {
        return repository.getDevicesOfASmarthomeType(type)
    }

}