package io.github.ppoonk.airgo_master.ui.configuration.backend

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.ac.utils.StringUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.repository.Repository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackendScreen() {
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val securityWidget by sharedVM.configurationVM.securityWidget.collectAsState()

    LaunchedEffect(Unit) {
        sharedVM.configurationVM.getSecurity()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                actions = {
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
                                        sharedVM.snackbarVM.openSnackbar("更新成功")
                                    }
                            }
                        },
                        enabled = securityWidget.isUpdateValid
                    ) {
                        ACIconSmall(ACIconDefault.Check, null)
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(it).padding(horizontal = 16.dp).imePadding(),
        ) {
            item {
                ACCard(
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Column(
                        Modifier.fillMaxWidth().padding(16.dp),
                    ) {
                        Text("tokenSign")
                        ACTextField(
                            value = securityWidget.tokenSign,
                            onValueChange = { sharedVM.configurationVM.refreshTokenSign(it) },
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        )
                        Text("tokenDuration")
                        ACTextField(
                            value = securityWidget.tokenDuration,
                            onValueChange = { sharedVM.configurationVM.refreshTokenDuration(it) },
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                        )
                        Text("adminPath")
                        ACTextField(
                            value = securityWidget.adminPath,
                            onValueChange = { sharedVM.configurationVM.refreshAdminPath(it) },
                            modifier = Modifier.padding(top = 8.dp),
                            trailingIcon = {
                                IconButton(onClick = {
                                    sharedVM.configurationVM.refreshAdminPath("/"+ StringUtils.newRandomString(8))
                                }) {
                                    ACIconSmall(ACIconDefault.Sync, null)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
