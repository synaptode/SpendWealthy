package com.spendwealthy.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppColors.CardBackground,
    hasCatEars: Boolean = false,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(22.dp),
                spotColor = Color.Black.copy(alpha = 0.06f),
                ambientColor = Color.Black.copy(alpha = 0.03f)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .padding(18.dp)
    ) {
        content()
    }
}
