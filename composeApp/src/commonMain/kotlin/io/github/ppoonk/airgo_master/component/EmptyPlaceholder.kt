package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.data_empty
import org.jetbrains.compose.resources.stringResource

@Composable
fun EmptyPlaceholder(message: String = stringResource(Res.string.data_empty)) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = Res.getUri("files/empty.svg"),
            contentDescription = null,
            modifier = Modifier.widthIn(max = 400.dp),
        )
        if (message.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(message, color = MaterialTheme.colorScheme.outline)
        }
    }
}