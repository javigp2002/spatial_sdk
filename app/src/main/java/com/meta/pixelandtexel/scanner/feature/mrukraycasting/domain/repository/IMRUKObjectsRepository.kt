package com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository

import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.ObjectEntityModel
import com.meta.pixelandtexel.scanner.models.devices.Device

interface IMRUKObjectsRepository {
    var lastAddedObjectDevice: Device?
    val mrukEntities: HashMap<String, ObjectEntityModel>
    suspend fun addMRUKObject(addObject: MrukRaycastModel): Boolean
    suspend fun deleteMRUKObject(objectId: String): Boolean
    suspend fun getAllMRUKObjects(): List<MrukRaycastModel>
}