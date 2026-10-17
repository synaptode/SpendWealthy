package com.spendwealthy.app.ui.components

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * High-performance, buttery smooth motion micro-interactions and transitions
 * designed for fintech luxury feel (Apple / Revolut standard).
 */
object MotionSpecs {
    val TabEnter = fadeIn(animationSpec = tween(220, easing = LinearOutSlowInEasing)) +
            scaleIn(initialScale = 0.98f, animationSpec = tween(220, easing = LinearOutSlowInEasing))
    val TabExit = fadeOut(animationSpec = tween(160, easing = FastOutLinearInEasing))
    val TabTransition = TabEnter.togetherWith(TabExit)

    val TactileSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val GentleSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessLow
    )

    val SmoothMeter = tween<Float>(
        durationMillis = 900,
        easing = FastOutSlowInEasing
    )
}

/**
 * Adds an Apple-grade tactile scale bounce when an interactive element is pressed.
 */
@Composable
fun Modifier.tactilePress(
    pressedScale: Float = 0.94f,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = MotionSpecs.TactileSpring,
        label = "TactilePressScale"
    )

    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

/**
 * Clickable modifier bundled with tactile spring press scale and optional haptic feedback.
 */
@Composable
fun Modifier.bouncyClickable(
    pressedScale: Float = 0.94f,
    hapticType: HapticFeedbackType? = HapticFeedbackType.TextHandleMove,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1f,
        animationSpec = MotionSpecs.TactileSpring,
        label = "BouncyClickableScale"
    )

    return this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled
        ) {
            hapticType?.let { haptic.performHapticFeedback(it) }
            onClick()
        }
}
