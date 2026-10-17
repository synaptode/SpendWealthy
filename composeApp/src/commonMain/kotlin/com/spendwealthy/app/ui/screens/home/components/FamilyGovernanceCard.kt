package com.spendwealthy.app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spendwealthy.app.domain.service.ConsensusRequest
import com.spendwealthy.app.ui.theme.AppColors
import com.spendwealthy.app.ui.theme.AppTypography

@Composable
fun FamilyGovernanceCard(
    requests: List<ConsensusRequest>,
    onApprove: (String) -> Unit = {},
    onReject: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = AppColors.Accent.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Accent.copy(alpha = 0.2f))
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(AppColors.Accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text("Family Governance", fontWeight = FontWeight.Black, color = AppColors.TextMain, fontSize = 14.sp)
                    Text("${requests.size} Pending Approvals", style = MaterialTheme.typography.labelSmall, color = AppColors.Accent)
                }
            }
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                requests.forEach { request ->
                    ConsensusItem(
                        request = request,
                        onApprove = onApprove,
                        onReject = onReject
                    )
                }
            }
        }
    }
}

@Composable
private fun ConsensusItem(
    request: ConsensusRequest,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("${request.requesterName} wants to spend", style = MaterialTheme.typography.labelSmall, color = AppColors.TextMuted)
                    Text(
                        text = request.amount.formatWithSymbol(),
                        style = AppTypography.bodyNumeric(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.TextMain
                        )
                    )
                }
                
                CircularProgressIndicator(
                    progress = { request.currentApprovals.toFloat() / request.approvalsRequired.toFloat() },
                    modifier = Modifier.size(24.dp),
                    color = AppColors.Accent,
                    strokeWidth = 3.dp,
                    trackColor = AppColors.Outline
                )
            }
            
            Text(request.purpose, style = MaterialTheme.typography.bodySmall, color = AppColors.TextMain)
            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onApprove(request.id) },
                    modifier = Modifier.weight(1f).height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Approve", fontSize = 12.sp)
                }
                
                OutlinedButton(
                    onClick = { onReject(request.id) },
                    modifier = Modifier.weight(1f).height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.Outline)
                ) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Reject", fontSize = 12.sp, color = AppColors.Error)
                }
            }
        }
    }
}
