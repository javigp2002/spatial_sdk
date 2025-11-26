package com.meta.pixelandtexel.scanner.android.views.components.smart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.meta.pixelandtexel.scanner.utils.mytheme.MyPaddings
import com.meta.spatial.uiset.theme.SpatialColor
import com.meta.spatial.uiset.theme.SpatialTheme
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.CloseCircle

@Composable
fun SmartHomeHeader(
    title: String,
    onClose: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            color = SpatialColor.white100,
            style = SpatialTheme.typography.headline1Strong,
            modifier = Modifier.padding(bottom = MyPaddings.XS),
        )
        Row {
            IconButton(onClick = { onClose?.invoke() }) {
                Image(SpatialIcons.Regular.CloseCircle, "Close panel")
            }
        }
    }
}
