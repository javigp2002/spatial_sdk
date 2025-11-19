package com.meta.pixelandtexel.scanner.objectdetection.repository

import com.meta.pixelandtexel.scanner.models.EntityData
import com.meta.pixelandtexel.scanner.models.ObjectInfoRequest
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose

/**
 * Defines the contract for a repository that manages the creation
 * and data handling of displayed entities in the scene, such as information panels.
 */
interface IDisplayedEntityRepository {
    /**
     * Holds the data for the next information panel to be displayed.
     * This data is consumed when a new panel entity is created.
     */
    var newViewModelData: EntityData?

    val entitiesHashMap: HashMap<Int, Entity>

    /**
     * Creates a generic information panel entity at a calculated position.
     * It also stores the provided data to be consumed by the panel's composable.
     *
     * @param panelId The resource ID for the panel registration.
     * @param data The information to be displayed on the panel.
     * @param rightEdgePose The pose of the camera's right edge, used as a reference for positioning.
     * @return The newly created panel [Entity].
     */
    fun createGenericInfoPanel(
        panelId: Int,
        data: ObjectInfoRequest,
        rightEdgePose: Pose
    ): Entity

    fun deleteEntity(entityId: Int)

}