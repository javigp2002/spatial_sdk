package com.meta.pixelandtexel.scanner.android.datasource.repository

import androidx.compose.ui.tooling.preview.Devices
import com.meta.pixelandtexel.scanner.datasource.network.SmartHomeApi
import com.meta.pixelandtexel.scanner.android.datasource.dto.EntityIdDto
import com.meta.pixelandtexel.scanner.android.domain.model.SmartPlugInfo
import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository
import com.meta.pixelandtexel.scanner.models.devices.Device

class SmartHomeRepositoryImpl (
    private val api: SmartHomeApi
) : SmartHomeRepository {

    override suspend fun getConnection(boolean: Boolean): Boolean {
       return true
    }

    override suspend fun turnOnSwitch(entityId: String): Boolean {
        try {
            api.turnOnSwitch(
                body = EntityIdDto(entity_id = entityId)
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun turnOffSwitch(entityId: String): Boolean {
        try {
            api.turnOffSwitch(
                body = EntityIdDto(entity_id = entityId)
            )
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun getSmartPlugInfo(entityId: String): SmartPlugInfo? {
        return null
    }

    override suspend fun getDevices(): List<Device> {
        val template = """
            {%- set javi_entities = states | selectattr("entity_id", "search") | map(attribute="entity_id") | list -%}
            {% set devices = javi_entities | map("device_id") | unique | reject("eq", None) | list -%}
            {% set ns = namespace(devices=[]) -%}
            {% for device in devices -%}
              {% set entities = device_entities(device) | list -%}
              {% if entities -%}
                {% set device_name = device_attr(device, "name_by_user") or device_attr(device, "name") or device -%}
                {% set ns.devices = ns.devices + [{"name": device_name, "entities": entities | sort}] -%}
              {% endif -%}
            {% endfor -%}
            {{ {"devices": ns.devices} | to_json }}

        """.trimIndent()

        try {
            val response = api.postTemplate(
                body = mapOf("template" to template)
            )


            return emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}