package io.github.ppoonk.airgo_master

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    java.util.Locale.setDefault(java.util.Locale.ENGLISH)
    Window(
        onCloseRequest = ::exitApplication,
        title = "Airgo Master",
    ) {
        App()
    }
}