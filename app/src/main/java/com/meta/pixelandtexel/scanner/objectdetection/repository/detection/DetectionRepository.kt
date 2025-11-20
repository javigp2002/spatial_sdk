package com.meta.pixelandtexel.scanner.objectdetection.repository.detection

import android.media.Image
import android.util.Log
import com.meta.pixelandtexel.scanner.objectdetection.detector.IObjectDetectorHelper
import com.meta.pixelandtexel.scanner.objectdetection.detector.models.DetectedObjectsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Data class to hold the complete result of a detection, including the image and the finalizer.
 */
data class DetectionState(
    val result: DetectedObjectsResult,
    val image: Image,
    val finally: () -> Unit
)

class ObjectDetectionRepository(private val detector: IObjectDetectorHelper): IObjectDetectionRepository {
    companion object {
        private const val TAG = "ObjectDetectionRepo"
    }

    private val isDetecting = AtomicBoolean(false)

    private val _detectionState = MutableStateFlow<DetectionState?>(null)
    override val detectionState: StateFlow<DetectionState?> = _detectionState

    override fun processImage(image: Image, width: Int, height: Int, finally: () -> Unit) {
        if (!isDetecting.compareAndSet(false, true)) {
            Log.v(TAG, "Frame dropped, detector busy.")
            finally()
            return
        }

        detector.detect(image, width, height) { result ->
            try {
                if (result != null) {
                    _detectionState.value = DetectionState(result, image, finally)
                } else {
                    finally()
                }
            } finally {
                isDetecting.set(false)
            }
        }
    }
}