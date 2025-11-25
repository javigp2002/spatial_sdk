package com.meta.pixelandtexel.scanner.feature.objectdetection

import android.animation.Keyframe
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.TextView
import com.meta.pixelandtexel.scanner.DiApplication
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.TrackedObject
import com.meta.pixelandtexel.scanner.ViewLocked
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection.ObjectDetectionRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.system.TrackedObjectSystem
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.system.ViewLockedSystem
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.camera.CameraController
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.camera.enums.CameraStatus
import com.meta.pixelandtexel.scanner.feature.objectdetection.domain.camera.models.CameraProperties
import com.meta.pixelandtexel.scanner.feature.objectdetection.utils.NumberSmoother
import com.meta.pixelandtexel.scanner.feature.objectdetection.android.views.android.CameraPreview
import com.meta.pixelandtexel.scanner.feature.objectdetection.android.views.android.GraphicOverlay
import com.meta.pixelandtexel.scanner.feature.objectdetection.android.views.android.ISurfaceProvider
import com.meta.spatial.core.ComponentRegistration
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SendRate
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Hittable
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A Spatial SDK Feature which uses the device camera feed and a CV object detection model to
 * discover objects in the user's surroundings, assign them labels and persistent ids, and track
 * their position over time.
 *
 * This class integrates camera functionalities, object detection processing, and user interface
 * elements related to displaying camera status and detected objects. It handles the camera
 * lifecycle (initialization, scanning, pausing), processes image frames for object detection
 *
 * The feature can optionally display a camera preview and debug information ([spawnCameraViewPanel]
 * = true) or rely on a [TrackedObjectSystem] to visualize detected objects in the spatial
 * environment. It provides callbacks for status changes ([onStatusChanged]), new object detections
 * ([onDetectedObjects]), and when a user selects a tracked object ([onTrackedObjectSelected]).
 *
 * It registers necessary UI panels for camera status and (optionally) camera view, and integrates
 * with [ViewLockedSystem] and [TrackedObjectSystem] to manage how these elements are presented to
 * the user in a spatial context.
 *
 * @param activity The main application activity, used for context and UI operations.
 * @param onStatusChanged Callback invoked when the camera's operational status changes (e.g.
 *   PAUSED, SCANNING).
 * @param onDetectedObjects Callback invoked when new objects are detected in a camera frame.
 *   should proceed. Defaults to true.
 * @param onTrackedObjectSelected Callback invoked when a tracked object is selected by the user,
 *   providing the object's label, a [Bitmap] crop, and its [Pose].
 * @param spawnCameraViewPanel If true, a debug panel showing the camera feed and detection overlays
 *   will be created. Otherwise, object visualization relies on [TrackedObjectSystem].
 */
class ObjectDetectionFeature(
    private val activity: AppSystemActivity,
    private val onStatusChanged: ((CameraStatus) -> Unit)? = null,
    private val spawnCameraViewPanel: Boolean = false,
) : SpatialFeature {
    companion object {
        private const val TAG = "ObjectDetectionFeature"
    }

    // our core services
    private val cameraController: CameraController

    // systems
    private lateinit var viewLockedSystem: ViewLockedSystem
    private lateinit var trackedObjectSystem: TrackedObjectSystem

    // status ui
    private var cameraStatusRootView: View? = null
    private var cameraStatusText: TextView? = null
    private var _cameraStatus: CameraStatus = CameraStatus.PAUSED
    val status: CameraStatus
        get() = _cameraStatus

    private lateinit var cameraStatusEntity: Entity

    // debug ui
    private var cameraPreviewView: CameraPreview? = null
    private var graphicOverlayView: GraphicOverlay? = null
    private val smoothedInferenceTime = NumberSmoother()
    private lateinit var cameraViewEntity: Entity

    private val subscriptionScope = CoroutineScope(Dispatchers.Main)

    private var di: DiApplication = activity.application as DiApplication
    private var displayRepository: IDisplayedEntityRepository
    private val detectionRepository: ObjectDetectionRepository

    init {
        cameraController = CameraController(activity)
        cameraController.onCameraPropertiesChanged += ::onCameraPropertiesChanged

        displayRepository = di.appContainer.displayedEntityRepository
        detectionRepository = di.appContainer.objectDetectRepository

        subscriptionScope.launch {
            detectionRepository.detectionState.collect { state ->
                if (state == null) return@collect
                try {
                    if (state.foundObjects.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch{trackedObjectSystem.onObjectsFound(state.foundObjects)}
                    }
                    if (state.updatedObjects.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch{
                            trackedObjectSystem.onObjectsUpdated(state.updatedObjects)
                        }
                    }
                    if (state.lostObjectIds.isNotEmpty()) {
                        CoroutineScope(Dispatchers.Main).launch{
                            trackedObjectSystem.onObjectsLost(state.lostObjectIds)
                        }
                    }

                } finally {
                    state.finally()
                }
            }
        }

        subscriptionScope.launch {
            cameraController.imageState.collect {
                if (it == null) {
                    return@collect
                }

                detectionRepository.processImage(it.image, it.width, it.height, it.finally)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        activity.registerPanel(
            PanelRegistration(R.layout.ui_camera_view) {
                // size our panel to the camera's output size
                val cameraOutputSize = cameraController.cameraOutputSize

                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInPx = cameraOutputSize.width
                    layoutHeightInPx = cameraOutputSize.height
                    // use the default texel density
                    width = cameraOutputSize.width / (PanelConfigOptions.EYEBUFFER_WIDTH * 0.5f)
                    height = cameraOutputSize.height / (PanelConfigOptions.EYEBUFFER_HEIGHT * 0.5f)
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    cameraPreviewView = rootView?.findViewById(R.id.preview_view)
                    graphicOverlayView = rootView?.findViewById(R.id.graphic_overlay)

                    // if the ui view is marked as gone in the view, change it to null so it isn't sent
                    // to the camera controller
                    if (cameraPreviewView?.visibility == GONE) {
                        cameraPreviewView = null
                    }

                    // start the camera automatically after initialization
                    this@ObjectDetectionFeature.scan()
                }
            }
        )

        activity.registerPanel(
            PanelRegistration(R.layout.ui_camera_status_view) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 100f
                    width = 0.1f
                    height = 0.05f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    cameraStatusRootView = rootView
                    cameraStatusText =
                        rootView?.findViewById(R.id.camera_status)
                            ?: throw RuntimeException("Missing camera status text view")
                }
            }
        )
    }

    override fun systemsToRegister(): List<SystemBase> {
        val systems = mutableListOf<SystemBase>()
        // setup our systems, and subscribe to relevant events

        viewLockedSystem = ViewLockedSystem()
        cameraController.onCameraPropertiesChanged += viewLockedSystem::onCameraPropertiesChanged
        systems.add(viewLockedSystem)

        // only use the trackedObjectSystem to draw the outlines and labels of detected objects if
        // we aren't displaying the camera debug view
        if (!spawnCameraViewPanel) {
            trackedObjectSystem = TrackedObjectSystem(activity)
            trackedObjectSystem.onTrackedObjectSelected += ::onTrackedObjectSelected
            cameraController.onCameraPropertiesChanged += trackedObjectSystem::onCameraPropertiesChanged

            systems.add(trackedObjectSystem)
        }

        return systems
    }

    override fun componentsToRegister(): List<ComponentRegistration> {
        return listOf(
            ComponentRegistration.createConfig<ViewLocked>(ViewLocked.Companion, SendRate.DEFAULT),
            ComponentRegistration.createConfig<TrackedObject>(
                TrackedObject.Companion,
                SendRate.DEFAULT,
            ),
        )
    }

    override fun onSceneReady() {
        // create the camera status panel entity

        cameraStatusEntity =
            Entity.createPanelEntity(
                R.layout.ui_camera_status_view,
                Transform(),
                Hittable(MeshCollision.NoCollision),
                ViewLocked(Vector3(-0.16f, 0.15f, 0.7f), Vector3(0f), false),
            )
    }

    /**
     * Start the device camera, and wait to receive any camera frames for CV analysis. If we haven't
     * initialized the camera controller yet, first do that, awaiting the onCameraPropertiesChanged
     * call to resume scanning.
     */
    fun scan() {
        if (cameraController.isInitialized) {
            cameraController.start(
                surfaceProviders = listOfNotNull(cameraPreviewView as? ISurfaceProvider),
            )
            updateCameraStatus(CameraStatus.SCANNING)
            return
        }

        cameraController.initialize()
    }

    /**
     * Pause the device camera and any object detection CV logic. Wait for the camera session and
     * inferencing to end before clearing the last results from the cache and overlay.
     *
     * @param immediate Whether or not to force the immediate clearing of the cache/overlay.
     */
    fun pause(immediate: Boolean = false) {
        if (!cameraController.isInitialized || !cameraController.isRunning) {
            return
        }

        cameraController.stop()
        updateCameraStatus(CameraStatus.PAUSED)

        if (immediate) {
            detectionRepository.clear()
            graphicOverlayView?.clear()
            trackedObjectSystem.clear()
        } else {
            // wait for the camera to stop, and then clear the results
            CoroutineScope(Dispatchers.Main).launch {
                delay(100L)
                detectionRepository.clear()
                graphicOverlayView?.clear()
                trackedObjectSystem.clear()
            }
        }
    }

    /**
     * Callback for when the device camera is initialized, and its properties are processed. Typically
     * only executed once when the user first starts scanning and accepts the camera access
     * permissions.
     *
     * @param properties The device camera properties, encapsulated in a [CameraProperties].
     */
    private fun onCameraPropertiesChanged(properties: CameraProperties) {
        // start immediately since we aren't spawning an overlay panel
        if (!spawnCameraViewPanel) {
            scan()
            return
        }

        if (::cameraViewEntity.isInitialized) {
            return
        }

        // create our panel here, so we can size it to the camera output

        val offsetPose = properties.getHeadToCameraPose()
        cameraViewEntity =
            Entity.createPanelEntity(
                R.layout.ui_camera_view,
                Transform(),
                Hittable(MeshCollision.NoCollision),
                ViewLocked(offsetPose.t, offsetPose.q.toEuler(), true),
            )
    }

    /**
     * Updates the camera status label displayed to the user at the top left of their view.
     *
     * @param newStatus The new [CameraStatus] status to display
     */
    private fun updateCameraStatus(newStatus: CameraStatus) {
        if (_cameraStatus == newStatus) {
            return
        }

        when (newStatus) {
            CameraStatus.PAUSED -> {
                cameraStatusText?.setText(R.string.camera_status_off)
            }

            CameraStatus.SCANNING -> {
                cameraStatusText?.setText(R.string.camera_status_on)
            }
        }

        // play a subtle pulse animation when the status changes
        cameraStatusRootView?.let {
            val durationMs = 250L
            val kf0 = Keyframe.ofFloat(0f, 1f)
            val kf1 = Keyframe.ofFloat(0.5f, 1.5f)
            val kf2 = Keyframe.ofFloat(1f, 1f)
            val pvhScaleX = PropertyValuesHolder.ofKeyframe("scaleX", kf0, kf1, kf2)
            val pvhScaleY = PropertyValuesHolder.ofKeyframe("scaleY", kf0, kf1, kf2)
            ObjectAnimator.ofPropertyValuesHolder(it, pvhScaleX, pvhScaleY).apply {
                duration = durationMs
                start()
            }
        }

        _cameraStatus = newStatus
        onStatusChanged?.invoke(newStatus)
    }

    /**
     * Called by the TrackedObjectSystem when a tracked object, which is outlined and labeled in the
     * user's field of view, is selected by the user.
     *
     * @param id The id of the tracked object. Use this to try to fetch from the detectedObjectCache a
     *   cropped image of the object in the next camera frame, and pass that to the owning activity.
     * @param pose A [Pose] representing the user's head position, and the direction vector from the
     *   head to the right edge of the detected object in the user's view, at eye level.
     */
    private fun onTrackedObjectSelected(id: Int, pose: Pose) {
        if (!cameraController.isRunning) {
            Log.w(TAG, "Camera isn't running")
            return
        }

        detectionRepository.requestInfoForObject(id, pose)
    }

    override fun onPauseActivity() {
        pause()
        super.onPauseActivity()
    }

    override fun onDestroy() {
        pause(true)
        cameraController.dispose()

        if (::cameraViewEntity.isInitialized) {
            cameraViewEntity.destroy()
        }
        if (::cameraStatusEntity.isInitialized) {
            cameraStatusEntity.destroy()
        }

        subscriptionScope.cancel()
        super.onDestroy()
    }
}
