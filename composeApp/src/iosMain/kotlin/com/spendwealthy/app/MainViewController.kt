package com.spendwealthy.app

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIColor

fun MainViewController() = ComposeUIViewController { App() }.apply {
    view.backgroundColor = UIColor(
        red = 247.0 / 255.0,
        green = 246.0 / 255.0,
        blue = 236.0 / 255.0,
        alpha = 1.0
    )
}
