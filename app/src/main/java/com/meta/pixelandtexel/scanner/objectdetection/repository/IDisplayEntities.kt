package com.meta.pixelandtexel.scanner.objectdetection.repository

import com.meta.pixelandtexel.scanner.models.ObjectInfoRequest
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose

/**
 * Defines the contract for a repository that manages the creation
 * and data handling of displayed entities in the scene, such as information panels.
 */
interface IDisplayedEntityRepository {

    /**
     * Retrieves the pending data intended for a ViewModel and clears it
     * to prevent reuse. This is typically called when a panel is being composed.
     *
     * @return The pending data, or null if there is none.
     */
    fun getAndClearPendingData(): Any?

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

}