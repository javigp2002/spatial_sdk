package com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.scanner.android.views.smarthome.dynamic_smart_thing.state.EntityUiModel
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.models.devices.domain.SensorDomain
import com.meta.pixelandtexel.scanner.models.devices.domain.SwitchDomain
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun DynamicSmartThingScreen(
    device: Device,
    viewModel: DynamicSmartThingViewmodel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(device) {
        viewModel.initialize(device)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MyPaddings.M)
    ) {
        Text(
            text = uiState.deviceName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = MyPaddings.M)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(MyPaddings.S)
        ) {
            items(
                uiState.entities.size,
                key = { index -> uiState.entities.elementAt(index) }
            ) { entity ->
                EntityRow(
                    entity = uiState.entities[entity],
                    onSwitchToggle = { newValue ->
                        viewModel.onSwitchToggled(uiState.entities[entity].id, newValue)
                    }
                )
            }
        }
    }
}

@Composable
fun EntityRow(
    entity: EntityUiModel,
    onSwitchToggle: (Boolean) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MyPaddings.M),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val icon = when (entity.domain) {
                    is SwitchDomain -> Icons.Default.Build
                    is SensorDomain -> Icons.Default.Info
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(MyPaddings.S))
                Column {
                    Text(
                        text = entity.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = entity.id,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (entity.isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    when (val domain = entity.domain) {
                        is SwitchDomain -> {
                            Switch(
                                checked = domain.value,
                                onCheckedChange = onSwitchToggle
                            )
                        }

                        is SensorDomain -> {
                            Text(
                                text = domain.value,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}