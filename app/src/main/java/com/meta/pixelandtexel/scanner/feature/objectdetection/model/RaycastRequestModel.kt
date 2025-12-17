package com.meta.pixelandtexel.scanner.feature.objectdetection.model

import com.meta.spatial.core.Vector3

data class RaycastRequestModel(
    val origin: Vector3,
    val direction: Vector3
)