package io.github.ppoonk.airgo_master.ui.user.user.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalDrawerState
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.component.rememberLazyListState
import io.github.ppoonk.airgo_master.navigation.toDetailUser
import io.github.ppoonk.airgo_master.navigation.toEditUser
import io.github.ppoonk.airgo_master.repository.Repository
import kotlinx.coroutines.launch


@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun UserScreen() {
    val drawerState = LocalDrawerState.current
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val userVM = sharedVM.userVM
    val list = userVM.userList.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(text = "用户")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    }) {
                        ACIconSmall(ACIconDefault.DrawerOpen, null)
                    }
                },
                actions = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // 搜索
                        IconButton(
                            onClick = {
                                // 打开搜索抽屉，初始化搜索参数
                                sharedVM.baseSearchVM.openSearchDrawer(
                                    getSearchHistory = Repository.local::getUserSearchHistory,
                                    setSearchHistory = Repository.local::setUserSearchHistory,
                                    onConfirm = sharedVM.userVM::refreshUpdateUserListReq
                                )
                                // 打开搜索抽屉后，获取搜索历史
                                sharedVM.baseSearchVM.getSearchHistory()
                            }) {
                            ACIconSmall(ACIconDefault.Search, null)
                        }
                        // 新建
                        IconButton(onClick = {
                            scope.launch {
                                sharedVM.userVM.initEditUser(EditType.CREATE)
                                navController.toEditUser()
                            }
                        }) { ACIconSmall(ACIconDefault.Plus, null) }
                    }
                }
            )
        }
    ) {

        LazyColumn(
            state = list.rememberLazyListState(),
            modifier = Modifier.fillMaxWidth().padding(it).padding(horizontal = 16.dp).imePadding(),
        ) {

            if (list.itemSnapshotList.isEmpty()) {
                item {
                    EmptyPlaceholder()
                }
            } else {
                items(list.itemSnapshotList.items) { u ->
                    ACCard(
                        modifier = Modifier.fillMaxWidth()
                            .clickable {
                                sharedVM.userVM.initEditUser(EditType.UPDATE, u)
                                navController.toDetailUser()
                            },
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ID: ${u.id}")
                                Text("Email: ${u.email}")
//                Text("创建时间: ${TimeUtils.toLocalDateString(user.createdAt)}")
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("状态: ${u.status}")
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}


