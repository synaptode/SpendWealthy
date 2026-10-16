package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.DiscoveryIntent
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun WealthCommandBar(
    onQueryChange: (String) -> Unit,
    intents: List<DiscoveryIntent>,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = AppColors.SurfaceRaised,
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.Terminal, null, tint = AppColors.Primary, modifier = Modifier.size(20.dp))
                
                BasicTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onQueryChange(it)
                        isExpanded = it.isNotBlank()
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = AppColors.TextMain,
                        fontSize = 15.sp
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        Box(Modifier.fillMaxWidth()) {
                            if (query.isEmpty()) {
                                Text(
                                    "Ask anything: 'Buy a car', 'Goal projection'...",
                                    fontSize = 14.sp,
                                    color = AppColors.TextMuted,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                
                if (query.isNotEmpty()) {
                    IconButton(onClick = { query = ""; onQueryChange(""); isExpanded = false }) {
                        Icon(Icons.Default.Search, null, tint = AppColors.TextMuted)
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded && intents.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = AppColors.Surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline),
                shadowElevation = 8.dp
            ) {
                Column(Modifier.padding(8.dp)) {
                    intents.forEach { intent ->
                        IntentItem(intent) {
                            // Execute action
                            isExpanded = false
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IntentItem(intent: DiscoveryIntent, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier.size(32.dp).clip(CircleShape).background(AppColors.Primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.AutoAwesome, null, tint = AppColors.Primary, modifier = Modifier.size(16.dp))
        }
        Column {
            Text(intent.actionLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AppColors.TextMain)
            intent.predictedResult?.let {
                Text(it, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
            }
        }
    }
}
