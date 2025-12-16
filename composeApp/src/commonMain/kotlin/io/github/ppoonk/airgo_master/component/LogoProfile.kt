package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.drawer_slogan
import io.github.ppoonk.airgo_master.logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LogoProfile(): Unit {
    AutoSizeFade(
        compact = { Expanded() },
        medium= { Medium() },
        expanded = { Expanded() },
    )
}


@Composable
private fun Expanded(): Unit {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding( start = 16.dp, end = 16.dp,top = 16.dp, bottom = 32.dp)
    ) {
        Image(
            painterResource(Res.drawable.logo),
            null,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            stringResource(Res.string.drawer_slogan),
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

@Composable
private fun Medium(): Unit {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding( start = 16.dp, end = 16.dp,top = 16.dp, bottom = 32.dp)
    ) {
        Image(
            painterResource(Res.drawable.logo),
            null,
            modifier = Modifier.size(40.dp)
        )
    }
}

