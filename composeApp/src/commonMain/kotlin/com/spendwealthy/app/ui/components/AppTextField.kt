package com.spendwealthy.app.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.spendwealthy.app.ui.theme.AppColors

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String? = null,
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = true,
    error: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = label?.let { { Text(it) } },
        placeholder = if (placeholder.isNotBlank()) ({ Text(placeholder) }) else null,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        isError = error != null,
        supportingText = error?.let {
            {
                Text(
                    text = it,
                    color = AppColors.Danger,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        keyboardOptions = keyboardOptions,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.Primary,
            unfocusedBorderColor = AppColors.Outline,
            errorBorderColor = AppColors.Danger,
            focusedContainerColor = AppColors.Surface,
            unfocusedContainerColor = AppColors.SurfaceRaised,
            errorContainerColor = AppColors.Surface,
            focusedLabelColor = AppColors.Primary,
            unfocusedLabelColor = AppColors.TextMuted,
            cursorColor = AppColors.Primary
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = AppColors.TextMain,
            fontWeight = FontWeight.SemiBold
        )
    )
}
