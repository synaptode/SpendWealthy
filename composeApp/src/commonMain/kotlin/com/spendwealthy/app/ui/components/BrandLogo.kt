package com.spendwealthy.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import spendwealthy.composeapp.generated.resources.Res
import spendwealthy.composeapp.generated.resources.ic_brand_amazon
import spendwealthy.composeapp.generated.resources.ic_brand_apple
import spendwealthy.composeapp.generated.resources.ic_brand_chatgpt
import spendwealthy.composeapp.generated.resources.ic_brand_disney
import spendwealthy.composeapp.generated.resources.ic_brand_github
import spendwealthy.composeapp.generated.resources.ic_brand_google
import spendwealthy.composeapp.generated.resources.ic_brand_microsoft
import spendwealthy.composeapp.generated.resources.ic_brand_netflix
import spendwealthy.composeapp.generated.resources.ic_brand_paypal
import spendwealthy.composeapp.generated.resources.ic_brand_spotify
import spendwealthy.composeapp.generated.resources.ic_brand_youtube

enum class KnownBrand(
    val displayName: String,
    val resource: DrawableResource,
    val isMonochrome: Boolean = false
) {
    NETFLIX("Netflix", Res.drawable.ic_brand_netflix),
    APPLE("Apple", Res.drawable.ic_brand_apple, isMonochrome = true),
    GOOGLE("Google", Res.drawable.ic_brand_google),
    SPOTIFY("Spotify", Res.drawable.ic_brand_spotify),
    YOUTUBE("YouTube", Res.drawable.ic_brand_youtube),
    AMAZON("Amazon", Res.drawable.ic_brand_amazon),
    CHATGPT("ChatGPT / OpenAI", Res.drawable.ic_brand_chatgpt),
    DISNEY_PLUS("Disney+", Res.drawable.ic_brand_disney),
    PAYPAL("PayPal", Res.drawable.ic_brand_paypal),
    GITHUB("GitHub", Res.drawable.ic_brand_github, isMonochrome = true),
    MICROSOFT("Microsoft", Res.drawable.ic_brand_microsoft);

    companion object {
        fun from(name: String?): KnownBrand? {
            if (name.isNullOrBlank()) return null
            val lower = name.trim().lowercase()
            return when {
                lower.contains("netflix") -> NETFLIX
                lower.contains("spotify") -> SPOTIFY
                lower.contains("youtube") -> YOUTUBE
                lower.contains("apple") || lower.contains("icloud") || lower.contains("app store") || lower.contains("macbook") || lower.contains("iphone") -> APPLE
                lower.contains("google") || lower.contains("gsuite") || lower.contains("gmail") || lower.contains("play store") -> GOOGLE
                lower.contains("amazon") || lower.contains("prime") || lower.contains("aws") -> AMAZON
                lower.contains("chatgpt") || lower.contains("openai") || lower.contains("gpt") -> CHATGPT
                lower.contains("disney") || lower.contains("hotstar") -> DISNEY_PLUS
                lower.contains("paypal") -> PAYPAL
                lower.contains("github") -> GITHUB
                lower.contains("microsoft") || lower.contains("xbox") || lower.contains("office 365") || lower.contains("onedrive") -> MICROSOFT
                else -> null
            }
        }
    }
}

/**
 * Reusable ultra-lightweight vector brand logo component.
 *
 * @param brand Known brand enum
 * @param modifier Compose Modifier (default size: 24.dp)
 * @param tint Optional tint color for monochrome logos (Apple, GitHub).
 */
@Composable
fun BrandLogo(
    brand: KnownBrand,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color? = null
) {
    val painter = painterResource(brand.resource)
    if (brand.isMonochrome && tint != null) {
        Icon(
            painter = painter,
            contentDescription = brand.displayName,
            modifier = modifier,
            tint = tint
        )
    } else {
        Image(
            painter = painter,
            contentDescription = brand.displayName,
            modifier = modifier,
            colorFilter = if (brand.isMonochrome && tint != null) ColorFilter.tint(tint) else null
        )
    }
}

/**
 * Convenience brand logo renderer by name or merchant query.
 * If brand is recognized, renders the official SVG logo.
 * If unrecognized, executes the [fallback] composable.
 */
@Composable
fun BrandLogo(
    name: String,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color? = null,
    fallback: @Composable () -> Unit = {}
) {
    val brand = KnownBrand.from(name)
    if (brand != null) {
        BrandLogo(brand = brand, modifier = modifier, tint = tint)
    } else {
        fallback()
    }
}
