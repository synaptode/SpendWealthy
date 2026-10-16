package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class ConfettiPiece(
    val color: Color,
    val x: Float,
    val y: Float,
    val size: Float,
    val speedX: Float,
    val speedY: Float,
    val rotation: Float,
    val rotationSpeed: Float
)

@Composable
fun ConfettiView(
    modifier: Modifier = Modifier,
    isRunning: Boolean = true
) {
    val confettiCount = 50
    val colors = listOf(Color(0xFF818CF8), Color(0xFF34D399), Color(0xFFFBBF24), Color(0xFFF87171), Color(0xFF60A5FA))
    
    val infiniteTransition = rememberInfiniteTransition()
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val confettiPieces = remember {
        List(confettiCount) {
            ConfettiPiece(
                color = colors.random(),
                x = Random.nextFloat(),
                y = -Random.nextFloat(),
                size = Random.nextFloat() * 10 + 10,
                speedX = (Random.nextFloat() - 0.5f) * 0.02f,
                speedY = Random.nextFloat() * 0.03f + 0.01f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = (Random.nextFloat() - 0.5f) * 10f
            )
        }
    }

    if (isRunning) {
        Canvas(modifier = modifier.fillMaxSize()) {
            confettiPieces.forEach { piece ->
                val currentY = ((piece.y + progress * piece.speedY * 500) % 1.2f) * size.height
                val currentX = (piece.x + progress * piece.speedX * 500) * size.width
                val currentRotation = piece.rotation + progress * piece.rotationSpeed * 100
                
                withTransform({
                    rotate(currentRotation, Offset(currentX, currentY))
                }) {
                    drawRect(
                        color = piece.color,
                        topLeft = Offset(currentX, currentY),
                        size = androidx.compose.ui.geometry.Size(piece.size, piece.size / 2)
                    )
                }
            }
        }
    }
}
