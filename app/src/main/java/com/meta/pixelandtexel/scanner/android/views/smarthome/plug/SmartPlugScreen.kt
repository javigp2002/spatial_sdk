package com.meta.pixelandtexel.scanner.android.views.smartplug

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings

@Composable
fun SmartPlugScreen(
    entityId: Int,
    viewModel: SmartPlugViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(entityId) {
        viewModel.initialize(entityId)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(MyPaddings.M),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(MyPaddings.L)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enchufe Inteligente",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(MyPaddings.L))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isPlugOn) "Encendido" else "Apagado",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(MyPaddings.XL))
                } else {
                    Switch(
                        checked = uiState.isPlugOn,
                        onCheckedChange = { viewModel.togglePlug() },
                        modifier = Modifier.size(MyPaddings.XL)
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = MyPaddings.M),
            )

            Text(
                text = "Consumo en tiempo real",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(MyPaddings.M))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                InfoColumn(label = "Consumo", value = uiState.consumptionW, unit = "W")
                InfoColumn(label = "Intensidad", value = uiState.currentA, unit = "A")
                InfoColumn(label = "Tensión", value = uiState.voltageV, unit = "V")
            }
        }
    }
}

@Composable
private fun InfoColumn(label: String, value: Float?, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value?.let { "%.2f %s".format(it, unit) } ?: "--",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}
