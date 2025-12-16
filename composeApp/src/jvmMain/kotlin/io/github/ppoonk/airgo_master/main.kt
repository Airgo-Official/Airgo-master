package io.github.ppoonk.airgo_master

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Airgo Master",
    ) {
        App()
    }
}