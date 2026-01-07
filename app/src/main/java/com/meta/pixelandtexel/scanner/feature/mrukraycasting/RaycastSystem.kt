package com.meta.pixelandtexel.scanner.feature.mrukraycasting

import android.net.Uri
import android.util.Log
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.IObjectDetectionRepository
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SystemBase
import com.meta.spatial.mruk.MRUKFeature
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.mruk.SurfaceType


/**
 * A system responsible for updating raycasting interactions in the MRUK environment.
 *
 * Listens for changes in `detectionRepository.raycastRequest`. When a request is received,
 * it casts a ray against the room's geometry and creates a permanent 3D entity
 * at the point of impact.
 *
 * @property mrukFeature The MRUK feature used for raycasting and room management.
 * @property detectionRepository El repositorio del que se leen las solicitudes de raycast.
 */
class UpdateRaycastSystem(
    private val mrukFeature: MRUKFeature,
    private val detectionRepository: IObjectDetectionRepository
) : SystemBase() {
    companion object {
        const val MAX_DISTANCE = Float.MAX_VALUE
    }

    override fun execute() {
        return

//        val currentRoom = mrukFeature.getCurrentRoom()
//        if (currentRoom == null) {
//            Log.w("UpdateRaycastSystem", "Cannot raycast, no current room available.")
////            detectionRepository.raycastRequest = null
//            return
//        }
//
//
//        val hit = mrukFeature.raycastRoom(
//            currentRoom.anchor.uuid,
//            origin = objectRequestedDirection.headPosition,
//            direction = objectRequestedDirection.direction,
//            maxDistance = MAX_DISTANCE,
//            SurfaceType.PLANE_VOLUME,
//        )
//
//        if (hit != null) {
//            val newMeshPose =
//                Pose(hit.hitPosition, Quaternion.lookRotation(hit.hitNormal.normalize()))
//            Entity.create(
//                listOf(
//                    Mesh(Uri.parse("arrow.glb")),
//                    Transform(newMeshPose),
//                    Visible(true)
//                )
//            )
//        } else {
//            Log.d("UpdateRaycastSystem", "Raycast did not hit any surface.")
//        }
//
//        detectionRepository.raycastRequest = null
    }
}