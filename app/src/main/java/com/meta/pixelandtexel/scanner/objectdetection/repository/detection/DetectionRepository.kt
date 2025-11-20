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

/**
 * Manages the object detection workflow.
 *
 * This repository coordinates with a detector worker ([IObjectDetectorHelper]) to process images.
 * It handles the concurrency logic to prevent multiple frames from being processed simultaneously,
 * ensuring that frames are dropped if the detector is busy.
 *
 * @param detector The worker implementation that performs the actual detection.
 */
class ObjectDetectionRepository(private val detector: IObjectDetectorHelper) {
    companion object {
        private const val TAG = "ObjectDetectionRepo"
    }

    private val isDetecting = AtomicBoolean(false)

    private val _detectionState = MutableStateFlow<DetectionState?>(null)
    val detectionState: StateFlow<DetectionState?> = _detectionState

    /**
     * Tries to process a new image frame. If the detector is already busy,
     * the frame is dropped by immediately invoking the `finally` callback.
     *
     * @param image The image to process.
     * @param width The width of the image.
     * @param height The height of the image.
     * @param finally The callback that must be executed to release the image resource.
     */
    fun processImage(image: Image, width: Int, height: Int, finally: () -> Unit) {
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