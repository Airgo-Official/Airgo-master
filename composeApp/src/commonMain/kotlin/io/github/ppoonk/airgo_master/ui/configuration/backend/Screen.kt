package io.github.ppoonk.airgo_master.ui.configuration.backend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.utils.StringUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.repository.Repository
import kotlinx.coroutines.launch
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.configuration_backend_admin_path
import io.github.ppoonk.airgo_master.configuration_backend_admin_path_placeholder
import io.github.ppoonk.airgo_master.configuration_backend_token_duration
import io.github.ppoonk.airgo_master.configuration_backend_token_sign
import io.github.ppoonk.airgo_master.success
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackendScreen() {
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val securityWidget by sharedVM.configurationVM.editSecurityWidget.collectAsState()

    LaunchedEffect(Unit) {
        sharedVM.configurationVM.getSecurity()
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            Repository.remote.updateSecurity(securityWidget.toSecurity())
                                .onFailure { r ->
                                    sharedVM.dialogVM.openDialog(
                                        title = { Text(r.code.toString()) },
                                        text = { Text(r.message) }
                                    )
                                }
                                .onSuccess {
                                    // 更新本地 adminPath
                                    Repository.local.setAdminPath(securityWidget.adminPath)
                                    sharedVM.snackbarVM.openSnackbar("Success!")
                                }
                        }
                    },
                    enabled = securityWidget.isUpdateValid
                ) {
                    ACIconSmall(ACIconDefault.Check, null)
                }
            }
        }
        item {
            Column(
                modifier = Modifier.widthIn(max = 600.dp)
            ) {
                Text(stringResource(Res.string.configuration_backend_token_sign))
                TextField(
                    value = securityWidget.tokenSign,
                    onValueChange = { sharedVM.configurationVM.tokenSign(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
                Text(stringResource(Res.string.configuration_backend_token_duration))
                TextField(
                    value = securityWidget.tokenDuration,
                    onValueChange = { sharedVM.configurationVM.tokenDuration(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                )
                Text(stringResource(Res.string.configuration_backend_admin_path))
                TextField(
                    value = securityWidget.adminPath,
                    onValueChange = { sharedVM.configurationVM.adminPath(it) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
                    trailingIcon = {
                        IconButton(onClick = {
                            sharedVM.configurationVM.adminPath(
                                "/" + StringUtils.newRandomString(
                                    8
                                )
                            )
                        }) {
                            ACIconSmall(ACIconDefault.Sync, null)
                        }
                    },
                    placeholder = { Text(stringResource(Res.string.configuration_backend_admin_path_placeholder)) }
                )
            }
        }
    }
}

