package com.meta.pixelandtexel.scanner.android.datasource.repository

import com.meta.pixelandtexel.scanner.android.datasource.SmartHomeApi
import com.meta.pixelandtexel.scanner.android.datasource.dto.EntityIdDto
import com.meta.pixelandtexel.scanner.android.domain.model.SmartPlugInfo
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class SmartHomeRepositoryImpl (
    private val api: SmartHomeApi
) : SmartHomeRepository {

    override suspend fun getConnection(boolean: Boolean): Boolean {
       return true
    }

    override suspend fun turnOnSwitch(entityId: String): Boolean {
        try {
            api.turnOnSwitch(
                body = EntityIdDto(entity_id = "switch.smart_plug_javi")
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun turnOffSwitch(entityId: String): Boolean {
        try {
            api.turnOffSwitch(
                body = EntityIdDto(entity_id = "switch.smart_plug_javi")
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun getSmartPlugInfo(entityId: String): SmartPlugInfo? {
        return null
    }
}