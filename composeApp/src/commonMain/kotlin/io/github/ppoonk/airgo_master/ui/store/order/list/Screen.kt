package io.github.ppoonk.airgo_master.ui.store.order.list

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACTopAppBar

@Composable
fun OrderScreen() {
    Scaffold(
        topBar = { OrderScreenTopBar() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(it).padding(horizontal = 16.dp).imePadding()
        ) {

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderScreenTopBar() {
    val scope = rememberCoroutineScope()
  ACTopAppBar(
      title = {},
      actions = {}
  )
}
