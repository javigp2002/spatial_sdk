package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource

import android.net.Uri
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.ObjectEntityModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository.IMRUKObjectsRepository
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.datasource.network.SmartHomeApi
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.spatial.core.Quaternion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MRUKObjectsRepositoryImpl(private val smartHomeApi: SmartHomeApi) : IMRUKObjectsRepository {

    override var lastAddedObjectId: String? = null

    override val mrukEntities: HashMap<String, ObjectEntityModel> = HashMap()


    override suspend fun addMRUKObject(addObject: MrukRaycastModel): Boolean {
        if (!mrukEntities.containsKey(addObject.device.name)) {

            val newMeshPose = Pose(addObject.pose.t, addObject.pose.q)

            val boxEntity = Entity.create(
                listOf(
                    Mesh(Uri.parse("mesh://box")),
                    Box(Vector3(.1f, .1f, 0.1f)),
                    Transform(newMeshPose),
                    Visible(true)
                )
            )

            val panelEntity = Entity.Companion.createPanelEntity(
                R.integer.object_panel_id,
                Transform(newMeshPose * Pose(Vector3(0f, 0.5f, 0f))),
                Grabbable(type = GrabbableType.PIVOT_Y)

            )


            val objectEntity = ObjectEntityModel(
                objectEntity = boxEntity,
                panelEntity = panelEntity
            )
            mrukEntities[addObject.device.name] = objectEntity

            lastAddedObjectId = addObject.device.name
            return true
        } else {
            return false
        }
    }

    override suspend fun deleteMRUKObject(objectId: String): Boolean {
        return if (mrukEntities.containsKey(objectId)) {
            val objectEntityModel = mrukEntities[objectId]
            objectEntityModel?.objectEntity?.destroy()
            objectEntityModel?.panelEntity?.destroy()
            mrukEntities.remove(objectId)
            true
        } else {
            false
        }
    }

    override suspend fun getAllMRUKObjects(): Boolean {
        withContext(Dispatchers.IO) {
            // llamar a la api
//           val models = smartHomeApi.getAllDevicesOfASmarthomeType()

            val smartThingRaycastModel = MrukRaycastModel(
                device = Device(
                    name = "Smart Thing 1",
                    entityList = emptyList()
                ),
                pose = Pose(
                    Vector3(-1.6329944f, 0.7017277f, -1.3612689f),
                    Quaternion(-0.1479072f, -0.14790718f, 0.6914648f, -0.69146466f)
                )
            )
            addMRUKObject(smartThingRaycastModel)
        }
        return true

    }
}