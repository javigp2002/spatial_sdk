package com.meta.pixelandtexel.scanner.android.domain.repository

interface SmartHomeRepository {
    suspend fun getConnection(boolean: Boolean): Boolean

    suspend fun turnOnSwitch(entityId: String): Boolean

    suspend fun turnOffSwitch(entityId: String): Boolean

    suspend fun getSmartPlugInfo(entityId: String): SmartPlugInfo?

}