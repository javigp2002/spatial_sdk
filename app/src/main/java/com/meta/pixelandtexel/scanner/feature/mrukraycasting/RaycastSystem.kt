package com.meta.pixelandtexel.scanner.feature.mrukraycasting

import android.net.Uri
import android.util.Log
import com.meta.pixelandtexel.scanner.utils.getRightController
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.mruk.MRUKHit
import com.meta.spatial.mruk.SurfaceType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible



/**
 * A system responsible for updating raycasting interactions in the MRUK (Meta Room Understanding Kit) environment.
 * This system handles raycasting from the user's right hand to detect surfaces and updates the position and visibility
 * of a mesh entity (e.g., an arrow) based on the raycast results.
 *
 * @property mrukFeature The MRUK feature used for raycasting and room management.
 * @property meshEntity The entity representing the mesh (e.g., an arrow) to be updated based on raycast results.
 */
class UpdateRaycastSystem(
    private val mrukFeature: MRUKFeature,
private var meshEntity: Entity? = null,) : SystemBase() {
    companion object{
        const val MAX_DISTANCE = Float.MAX_VALUE
    }

    override fun execute() {
        meshEntity?.setComponent(Visible(false))

        val rightHandPose = getRightController(mrukFeature.systemManager)?.tryGetComponent<Transform>()?.transform
            ?: return
        val currentRoom = mrukFeature.getCurrentRoom() ?: return

        val rightHandDirection = (rightHandPose.q * Vector3(0f, 0f, 1f)).normalize()
        val hit =
            mrukFeature.raycastRoom(
                currentRoom.anchor.uuid,
                rightHandPose.t,
                rightHandDirection,
                MAX_DISTANCE,
                SurfaceType.PLANE_VOLUME,

            )
        if (hit != null) {
            if (meshEntity == null) {
                meshEntity = Entity.create(listOf(Mesh(Uri.parse("arrow.glb")), Transform(Pose())))
            }
            val arrowPose = Pose(hit.hitPosition, Quaternion.lookRotation(hit.hitNormal.normalize()))
            meshEntity?.setComponent(Transform(arrowPose))
            meshEntity?.setComponent(Visible(true))
        }

    }
}