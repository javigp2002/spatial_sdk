package com.meta.pixelandtexel.scanner.android.domain.repository

import com.meta.pixelandtexel.scanner.android.domain.model.SmartPlugInfo

interface SmartHomeRepository {
    suspend fun getConnection(boolean: Boolean): Boolean

    suspend fun turnOnSwitch(entityId: String): Boolean

    suspend fun turnOffSwitch(entityId: String): Boolean

    suspend fun getSmartPlugInfo(entityId: String): SmartPlugInfo?

}