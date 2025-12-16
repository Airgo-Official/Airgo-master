package io.github.ppoonk.airgo_master.ui.persion.me

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.navigation.toSignIn
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.ui.user.user.details.UserInfoProfile
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            ACTopAppBar(
                title = { Text("个人中心") },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            navController.popBackStack()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.AngleLeft,null)
                    }
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            item {
                val sharedVM = LocalSharedVM.current
                val signedInUser by sharedVM.userVM.signedInUser.collectAsState()
                signedInUser?.let {
                    UserInfoProfile(it)
                }
                Spacer(Modifier.height(16.dp))
            }
            item {
                MeScreenMenu()
            }
        }
    }
}


@Composable
fun MeScreenMenu(): Unit {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(
            MaterialTheme.colorScheme.surfaceContainerLowest,
            RoundedCornerShape(12.dp)
        ).padding(vertical = 16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ACIconSmall(ACIconDefault.Shield, null, modifier = Modifier.weight(1f))
            Text("修改密码", modifier = Modifier.weight(4f))
            ACIconSmall(ACIconDefault.AngleRight, null, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                scope.launch {
               // TODO
                }
            }
        ) {
            ACIconSmall(ACIconDefault.Bug, null, modifier = Modifier.weight(1f))
            Text("日志调试", modifier = Modifier.weight(4f))
            ACIconSmall(ACIconDefault.AngleRight, null, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(16.dp))

        ACButtonError(
            onClick = {
                scope.launch {
                    sharedVM.dialogVM.openDialog(
                        title = { Text("注意！") },
                        text = { Text("确定退出吗？") },
                    ) {
                        ACButtonError(
                            onClick = {
                                scope.launch {
                                    Repository.local.setToken(null)
                                    sharedVM.dialogVM.closeDialog()
                                    navController.toSignIn(Routes.Main)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(12.dp)
                        )
                        {
                            Text("确定")
                        }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        ) {
            ACIconSmall(ACIconDefault.SignOut,null)
            Spacer(Modifier.width(20.dp))
            Text("退出")
        }
    }
}



