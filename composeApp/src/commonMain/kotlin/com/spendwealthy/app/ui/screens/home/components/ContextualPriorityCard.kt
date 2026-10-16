package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.FinancialContext
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun ContextualPriorityCard(
    context: FinancialContext,
    modifier: Modifier = Modifier
) {
    val config = when (context) {
        FinancialContext.PAYDAY_WEEK -> PriorityConfig(
            title = "Payday Optimization",
            description = "It's payday! Now is the best time to sweep excess cash to your high-priority goals.",
            icon = Icons.Default.ElectricBolt,
            color = AppColors.Primary
        )
        FinancialContext.MONTH_END_CRUNCH -> PriorityConfig(
            title = "Budget Shield Active",
            description = "Month-end detected. We recommend shielding Rp 1.5M for upcoming fixed bills.",
            icon = Icons.Default.Warning,
            color = AppColors.Error
        )
        FinancialContext.FINANCIAL_ZEN -> PriorityConfig(
            title = "Wealth Momentum",
            description = "You're in the 'Zen' zone. All goals are on track. Consider a small reward or a new investment.",
            icon = Icons.Default.AutoAwesome,
            color = AppColors.Success
        )
        FinancialContext.HIGH_VELOCITY_SPENDING -> PriorityConfig(
            title = "Spending Alert",
            description = "Spending velocity is high. Review your recent transactions to maintain your resilience.",
            icon = Icons.Default.Info,
            color = AppColors.Accent
        )
        FinancialContext.STANDARD -> return
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = config.color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, config.color.copy(alpha = 0.3f))
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier.size(44.dp).clip(CircleShape).background(config.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(config.icon, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(config.title, fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 15.sp)
                Text(config.description, style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted, lineHeight = 16.sp)
            }
        }
    }
}

private data class PriorityConfig(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)
