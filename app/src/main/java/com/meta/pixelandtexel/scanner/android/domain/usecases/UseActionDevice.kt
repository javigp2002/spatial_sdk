package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class UseActionDevice(
    private val repository: SmartHomeRepository
) {
    suspend fun run(thingId: String, action: String): Boolean {
        return repository.getActionForThing(thingId, action)
    }

}