package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import javax.inject.Inject

class GetConnectionUsecase @Inject constructor(
    private val repository: SmartHomeRepository
) {
    suspend fun run(): Boolean {
        return repository.getConnection()
    }

}