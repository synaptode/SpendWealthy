package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.model.Money
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun SafeToSpendCard(
    amount: Money,
    daysRemaining: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                ambientColor = Color(0xFF10B981).copy(alpha = 0.2f),
                spotColor = Color(0xFF10B981).copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFFECFDF5)) // Very light mint
            .padding(24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF10B981).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = "SAFE FOR TREATS 🐟",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF065F46),
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = amount.format(),
                style = MaterialTheme.typography.headlineLarge,
                color = Color(0xFF064E3B),
                fontWeight = FontWeight.Black,
                fontSize = 36.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "pounce-worthy balance",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF059669).copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$daysRemaining days until next gajian 🐈",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}
