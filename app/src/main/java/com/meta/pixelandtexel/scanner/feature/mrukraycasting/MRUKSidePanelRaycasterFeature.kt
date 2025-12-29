package com.meta.pixelandtexel.scanner.feature.mrukraycasting

import android.os.Bundle
import com.meta.pixelandtexel.scanner.DiApplication
import com.meta.pixelandtexel.scanner.R
import com.meta.pixelandtexel.scanner.android.views.smarthome.plug.SmartPlugScreen
import com.meta.pixelandtexel.scanner.android.views.smarthome.plug.SmartPlugViewModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository.IMRUKObjectsRepository
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.ComponentRegistration
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.SystemBase
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.PanelShapeLayerBlendType
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.PanelRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get

/**
 * A Spatial SDK Feature which uses the device camera feed and a CV object detection model to
 * discover objects in the user's surroundings, assign them labels and persistent ids, and track
 * their position over time.
 *
 **/

class MRUKSidePanelRaycasterFeature(
    private val activity: AppSystemActivity,
) : SpatialFeature {
    companion object {
        private const val TAG = "ObjectDetectionFeature"
    }

    private val subscriptionScope = CoroutineScope(Dispatchers.Main)

    private var di: DiApplication = activity.application as DiApplication
    private val mrukObjectRepository: IMRUKObjectsRepository

    init {
        mrukObjectRepository = di.get()
        subscriptionScope.launch {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        activity.registerPanel(
            PanelRegistration(R.integer.object_panel_id) {
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
                    val id = mrukObjectRepository.lastAddedObjectId ?: return@composePanel
                    mrukObjectRepository.lastAddedObjectId = null

                    setContent {
                        val smartPlugViewModel = SmartPlugViewModel(di.get(), di.get())
                        SmartPlugScreen(
                            entityId = id,
                            viewModel = smartPlugViewModel
                        )
                    }
                }
            },
        )
    }

    suspend fun addSmartThing(device: Device, spawnPose: Pose) {
        mrukObjectRepository.addMRUKObject(
            MrukRaycastModel(
                device = device,
                pose = spawnPose
            )
        )
    }

    suspend fun getAllSmartThings(): Boolean {
        return mrukObjectRepository.getAllMRUKObjects()
    }

    override fun systemsToRegister(): List<SystemBase> {
        val systems = mutableListOf<SystemBase>()
        return systems
    }

    override fun componentsToRegister(): List<ComponentRegistration> {
        return listOf()
    }

    override fun onSceneReady() {
    }


    override fun onPauseActivity() {
        super.onPauseActivity()
    }

    override fun onDestroy() {

        subscriptionScope.cancel()
        super.onDestroy()
    }
}
