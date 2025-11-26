package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.meta.pixelandtexel.scanner.utils.mytheme.AppTextStyles

@Composable
fun ControlRow(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    thumbColor: Color,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = textColor,
            style= AppTextStyles.Subtitle,
            modifier = Modifier.weight(0.4f),
        )

        Slider(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(0.6f),
            colors = SliderDefaults.colors(
                thumbColor = thumbColor,
                activeTrackColor = Color.Gray,
                inactiveTrackColor = Color.LightGray
            )
        )
    }
}