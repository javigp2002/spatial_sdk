package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class GetConnectionUsecase (
    private val repository: SmartHomeRepository
) {
    suspend fun run(boolean: Boolean): Boolean {
        return repository.getConnection(boolean)
    }

}