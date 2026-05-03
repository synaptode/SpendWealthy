package com.spendwealthy.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun CatSnackbar(
    snackbarData: SnackbarData,
    icon: ImageVector = Icons.Default.Info
) {
    AppCard(
        hasCatEars = true,
        backgroundColor = AppColors.Dark.Background,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(AppColors.Primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = snackbarData.visuals.message,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.weight(1f)
            )
            
            snackbarData.visuals.actionLabel?.let { action ->
                TextButton(onClick = { snackbarData.performAction() }) {
                    Text(action, color = AppColors.Primary, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}
