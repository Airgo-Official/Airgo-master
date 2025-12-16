package io.github.ppoonk.airgo_master.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.defaultAvatar
import io.github.ppoonk.airgo_master.navigation.toMe
import io.github.ppoonk.airgo_master.repository.remote.model.User
import io.github.ppoonk.airgo_master.sharedViewModel.SharedVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
fun UserAccountProfile(): Unit {
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val navController = LocalNavController.current
    val signedInUser by sharedVM.userVM.signedInUser.collectAsState()

    AutoSizeFade(
        compact = { Expanded(scope, sharedVM, navController, signedInUser) },
        medium = { Medium(scope, sharedVM, navController, signedInUser) },
        expanded = { Expanded(scope, sharedVM, navController, signedInUser) },
    )

}


@Composable
private fun Medium(
    scope: CoroutineScope,
    sharedVM: SharedVM,
    navController: NavHostController,
    signedInUser: User?
): Unit {
    signedInUser?.let {
        Row(modifier = Modifier.padding(16.dp)) {
            AsyncImage(
                it.avatar,
                null,
                modifier = Modifier.size(40.dp).clip(CircleShape).clickable {
                    scope.launch {
                        navController.toMe()
                    }
                },
                error = painterResource(Res.drawable.defaultAvatar),
                placeholder = painterResource(Res.drawable.defaultAvatar),
            )
        }
    }
}

@Composable
private fun Expanded(
    scope: CoroutineScope,
    sharedVM: SharedVM,
    navController: NavHostController,
    signedInUser: User?
): Unit {

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp).height(40.dp).fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
            .clickable {
                scope.launch {
                    navController.toMe()
                }
            }
    ) {
        signedInUser?.let {
            AsyncImage(
                it.avatar,
                null,
                modifier = Modifier.padding(end = 8.dp).size(40.dp).clip(CircleShape),
                error = painterResource(Res.drawable.defaultAvatar),
                placeholder = painterResource(Res.drawable.defaultAvatar),
            )
            Text(
                it.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}