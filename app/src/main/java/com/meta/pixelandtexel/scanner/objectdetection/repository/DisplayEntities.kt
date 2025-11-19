package com.meta.pixelandtexel.scanner.objectdetection.repository

import com.meta.pixelandtexel.scanner.models.ObjectInfoRequest
import com.meta.pixelandtexel.scanner.utils.MathUtils.fromAxisAngle
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import kotlin.math.PI

object DisplayedEntityRepository: IDisplayedEntityRepository {


    // Almacén temporal para los datos del ViewModel
    private var pendingViewModelData: Any? = null

    // Constantes de dimensiones de panel
    private val INFO_PANEL_WIDTH = 0.632f

    /**
     * Obtiene y limpia los datos pendientes del ViewModel.
     * Llamado desde el 'composePanel' en PanelRegistration.
     */
    override fun getAndClearPendingData(): Any? {
        val data = pendingViewModelData
        pendingViewModelData = null
        return data
    }

    /**
     * Crea un panel de información genérico
     */
    override fun createGenericInfoPanel(
        panelId: Int, // R.integer.info_panel_id
        data: ObjectInfoRequest,
        rightEdgePose: Pose
    ): Entity {
        val spawnPose = getPanelSpawnPosition(rightEdgePose, INFO_PANEL_WIDTH)
        this.pendingViewModelData = data

        return Entity.Companion.createPanelEntity(
            panelId,
            Transform(spawnPose),
            Grabbable(type = GrabbableType.PIVOT_Y)
        )
    }



    /**
     * Calcula la pose del panel.
     * Lógica movida desde MainActivity.
     */
    private fun getPanelSpawnPosition(
        rightEdgePose: Pose,
        panelWidth: Float,
        zDistance: Float = 1f,
    ): Pose {
        // get angle based on arc length of panel width / 2 at z distance
        val angle = (panelWidth / 2) / zDistance

        // rotate the pose forward direction by angle to get the new forward direction
        val newFwd =
            Quaternion.Companion.fromAxisAngle(Vector3.Companion.Up, angle * 180f / PI.toFloat())
                .times(rightEdgePose.forward())
                .normalize()

        // apply offset to lower the panel to eye height
        val position = rightEdgePose.t - Vector3(0f, 0.1f, 0f) + newFwd * zDistance
        val rotation = Quaternion.Companion.lookRotationAroundY(newFwd)

        return Pose(position, rotation)
    }
}