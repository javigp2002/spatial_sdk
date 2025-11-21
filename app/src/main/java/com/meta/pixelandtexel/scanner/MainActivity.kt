package com.meta.pixelandtexel.scanner

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import com.meta.pixelandtexel.scanner.ecs.OutlinedSystem
import com.meta.pixelandtexel.scanner.ecs.WristAttachedSystem
import com.meta.pixelandtexel.scanner.objectdetection.ObjectDetectionFeature
import com.meta.pixelandtexel.scanner.objectdetection.camera.enums.CameraStatus
import com.meta.pixelandtexel.scanner.objectdetection.repository.IDisplayedEntityRepository
import com.meta.pixelandtexel.scanner.services.TipManager
import com.meta.pixelandtexel.scanner.services.UserEvent
import com.meta.pixelandtexel.scanner.services.settings.SettingsService
import com.meta.pixelandtexel.scanner.viewmodels.ObjectInfoViewModel
import com.meta.pixelandtexel.scanner.views.objectinfo.ObjectInfoScreen
import com.meta.pixelandtexel.scanner.views.welcome.WelcomeScreen
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.compose.panelViewLifecycleOwner
import com.meta.spatial.core.Entity
import com.meta.spatial.core.SendRate
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.vr.LocomotionSystem
import com.meta.spatial.vr.VRFeature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File

/**
 * Main entry point for the Quest application. See the README for an in-depth description for how
 * this application functions, and see the official
 * [Meta Spatial SDK documentation](https://developers.meta.com/horizon/develop/spatial-sdk) for how
 * to build Spatial applications, or convert your existing Android application to function in the
 * Quest headset.
 */
class MainActivity : ActivityCompat.OnRequestPermissionsResultCallback, AppSystemActivity() {
    companion object {
        private const val TAG = "MainActivity"

        private const val PERMISSIONS_REQUEST_CODE = 1000
        private val PERMISSIONS_REQUIRED = arrayOf("horizonos.permission.HEADSET_CAMERA")
    }

    // used for scene inflation
    private var gltfxEntity: Entity? = null
    private val activityScope = CoroutineScope(Dispatchers.Main)

    private lateinit var permissionsResultCallback: (granted: Boolean) -> Unit

    // button for toggling the scanning
    private var cameraControlsBtn: ImageButton? = null

    // our main scene entities
    private var welcomePanelEntity: Entity? = null

    // our main services for detected object, displaying helpful tips, and displaying pre-assembled
    // panel content for select objects (with 3D models)
    private lateinit var objectDetectionFeature: ObjectDetectionFeature
    private lateinit var tipManager: TipManager

    lateinit var entityRepository: IDisplayedEntityRepository

    override fun registerFeatures(): List<SpatialFeature> {
        objectDetectionFeature =
            ObjectDetectionFeature(
                this,
                onStatusChanged = ::onObjectDetectionFeatureStatusChanged,
            )

        return listOf(VRFeature(this), ComposeFeature(), objectDetectionFeature)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SettingsService.initialize(this)

        NetworkedAssetLoader.init(
            File(applicationContext.cacheDir.canonicalPath),
            OkHttpAssetFetcher()
        )

        // extra object detection handling and usability
        entityRepository = (application as DiApplication).appContainer.displayedEntityRepository
        tipManager =
            TipManager(this) {
                stopScanning()
            }

        // register systems/components

        systemManager.unregisterSystem<LocomotionSystem>()

        // FIXME not working; prevent isdk components from automatically being added to all panels
        // systemManager.findSystem<IsdkToolkitBridgeSystem>().active = false

        componentManager.registerComponent<WristAttached>(WristAttached.Companion, SendRate.DEFAULT)
        systemManager.registerSystem(WristAttachedSystem())

        componentManager.registerComponent<Outlined>(Outlined.Companion, SendRate.DEFAULT)
        systemManager.registerSystem(OutlinedSystem(this))

        // wait for GLXF to load before accessing nodes inside it

        loadGLXF().invokeOnCompletion {
            val composition = glXFManager.getGLXFInfo("scanner_app_main_scene")

            // wait for system manager to initialize so we can get the underlying scene objects

            welcomePanelEntity = composition.getNodeByName("WelcomePanel").entity
        }
    }

    override fun onSceneReady() {
        super.onSceneReady()

        // set the reference space to enable re-centering
        scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

        scene.setLightingEnvironment(
            ambientColor = Vector3(0f),
            sunColor = Vector3(0f),
            sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
            environmentIntensity = 0.2f,
        )
        scene.updateIBLEnvironment("museum_lobby.env")

        scene.setViewOrigin(0.0f, 0.0f, 0.0f, 180.0f)

        scene.enablePassthrough(true)
    }

    override fun registerPanels(): List<PanelRegistration> {
        return listOf(
            PanelRegistration(R.integer.welcome_panel_id) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 368f
                    width = 0.368f
                    height = 0.404f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                    effectShader = "customPanel.frag" // just for demonstration purposes
                }
                composePanel {
                    setContent {
                        CompositionLocalProvider(
                            LocalOnBackPressedDispatcherOwner provides
                                    object : OnBackPressedDispatcherOwner {
                                        override val lifecycle: Lifecycle
                                            get() = this@MainActivity.panelViewLifecycleOwner.lifecycle

                                        override val onBackPressedDispatcher: OnBackPressedDispatcher
                                            get() = OnBackPressedDispatcher()
                                    }
                        ) {
                            WelcomeScreen(
                                onLinkClicked = {
                                    val uri = it.toUri()
                                    val browserIntent = Intent(Intent.ACTION_VIEW, uri)
                                    startActivity(browserIntent)
                                }
                            ) {
                                welcomePanelEntity?.destroy()
                                welcomePanelEntity = null
                            }
                        }
                    }
                }
            },
            PanelRegistration(R.layout.ui_help_button_view) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 80f
                    width = 0.04f
                    height = 0.04f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    val helpBtn =
                        rootView?.findViewById<ImageButton>(R.id.help_btn)
                            ?: throw RuntimeException("Missing help button")

                    helpBtn.setOnClickListener {
                        welcomePanelEntity?.destroy()
                        welcomePanelEntity = null
                        stopScanning()
                        tipManager.dismissTipPanels()

                        tipManager.showHelpPanel()
                    }
                }
            },
            PanelRegistration(R.layout.ui_camera_controls_view) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 80f
                    width = 0.04f
                    height = 0.04f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                panel {
                    cameraControlsBtn =
                        rootView?.findViewById(R.id.camera_play_btn)
                            ?: throw RuntimeException("Missing camera play/pause button")

                    cameraControlsBtn?.setOnClickListener {
                        welcomePanelEntity?.destroy()
                        welcomePanelEntity = null

                        when (objectDetectionFeature.status) {
                            CameraStatus.PAUSED -> {
                                // first ask permission if we haven't already
                                if (!hasPermissions()) {
                                    this@MainActivity.requestPermissions { granted ->
                                        if (granted) {
                                            startScanning()
                                        }
                                    }

                                    return@setOnClickListener
                                }

                                startScanning()
                            }

                            CameraStatus.SCANNING -> {
                                stopScanning()
                            }
                        }
                    }
                }
            },
            PanelRegistration(R.integer.info_panel_id) {
                config {
                    themeResourceId = R.style.PanelAppThemeTransparent
                    includeGlass = false
                    layoutWidthInDp = 632f
                    width = 0.632f
                    height = 0.644f
                    layerConfig = LayerConfig()
                    layerBlendType = PanelShapeLayerBlendType.MASKED
                    enableLayerFeatheredEdge = true
                }
                composePanel {
                    stopScanning()
                    val entityData = entityRepository.newViewModelData ?: return@composePanel

                    val vm = ObjectInfoViewModel(
                        entityData.data,
                        getString(R.string.object_query_template)
                    )

                    setContent {
                        ObjectInfoScreen(
                            vm,
                            onResume = { },
                            onClose = {
                                entityRepository.deleteEntity(entityData.entityId)
                            },
                        )
                    }
                }
            },
        )
    }


    /** Activates the object detection feature scanning, which turns on the user's camera. */
    private fun startScanning() {
        objectDetectionFeature.scan()
        tipManager.reportUserEvent(UserEvent.STARTED_SCANNING)
    }

    /** Stops the object detection and device camera. */
    private fun stopScanning() {
        objectDetectionFeature.pause()
    }

    /**
     * Executed when the object detection feature has scanning status has changed.
     *
     * @param newStatus The new [CameraStatus] camera scanning status
     */
    private fun onObjectDetectionFeatureStatusChanged(newStatus: CameraStatus) {
        cameraControlsBtn?.setBackgroundResource(
            when (newStatus) {
                CameraStatus.PAUSED -> com.meta.spatial.uiset.R.drawable.ic_play_circle_24
                CameraStatus.SCANNING -> com.meta.spatial.uiset.R.drawable.ic_pause_circle_24
            }
        )
    }


    override fun onPause() {
        stopScanning()
        super.onPause()
    }

    private fun loadGLXF(): Job {
        gltfxEntity = Entity.Companion.create()
        return activityScope.launch {
            glXFManager.inflateGLXF(
                "apk:///scenes/Composition.glxf".toUri(),
                rootEntity = gltfxEntity!!,
                keyName = "scanner_app_main_scene",
            )
        }
    }

    // permissions requesting

    private fun hasPermissions() =
        PERMISSIONS_REQUIRED.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }

    private fun requestPermissions(callback: (granted: Boolean) -> Unit) {
        permissionsResultCallback = callback

        ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d(TAG, "Camera permission granted")
                    permissionsResultCallback(true)
                } else {
                    Log.w(TAG, "Camera permission denied")
                    permissionsResultCallback(false)
                }
            }
        }
    }
}