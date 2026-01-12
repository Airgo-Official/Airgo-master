package io.github.ppoonk.airgo_master

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class NativePlatform : Platform {
    @Composable
    override fun HorizontalScrollbar(state: LazyListState, modifier: Modifier) {
    }

    @Composable
    override fun VerticalScrollbar(state: LazyListState, modifier: Modifier) {
    }
}

actual fun Platform(): Platform = NativePlatform()