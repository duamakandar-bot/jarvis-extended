package com.example.ui.components

import androidx.compose.runtime.Composable

/**
 * Backwards compatibility wrapper for [DeXConnectionHelperDialog].
 * Provides the interactive step-by-step tutorial on how to enable Samsung Wireless DeX via Quick Settings.
 */
@Composable
fun DexConnectionHelpDialog(
    onDismiss: () -> Unit,
    onActivateDesktopMode: () -> Unit
) {
    DeXConnectionHelperDialog(
        onDismiss = onDismiss,
        onActivateDesktopMode = onActivateDesktopMode
    )
}
