package com.meta.pixelandtexel.scanner.objectdetection.repository.detection

import android.media.Image
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObject

data class DetectionState(
    val image: Image,
    val foundObjects: List<DetectedObject>,
    val updatedObjects: List<DetectedObject>,
    val lostObjectIds: List<Int>,
    val finally: () -> Unit,
)