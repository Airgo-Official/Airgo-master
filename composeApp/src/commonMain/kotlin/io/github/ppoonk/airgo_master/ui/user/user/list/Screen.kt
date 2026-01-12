package io.github.ppoonk.airgo_master.ui.user.user.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.ui.component.AutoSizeFade
import io.github.ppoonk.ac.utils.TimeUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalDrawerState
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalPlatform
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.BaseSearchDrawer
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.EmptyPlaceholder
import io.github.ppoonk.airgo_master.delete
import io.github.ppoonk.airgo_master.delete_confirmation
import io.github.ppoonk.airgo_master.navigation.toEditUser
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.UserTableColumn
import io.github.ppoonk.airgo_master.user_list
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UserListScreen() {
    val drawerState = LocalDrawerState.current
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val platform = LocalPlatform.current
    val scope = rememberCoroutineScope()
    val userVM = sharedVM.userVM
    val list = userVM.userList.collectAsLazyPagingItems()
    val rowState = rememberLazyListState()
    val columnState = rememberLazyListState()

    // TODO 删除其他键盘padding
    Scaffold(
        topBar = {
            ACTopAppBar(
                title = {
                    Text(text = stringResource(Res.string.user_list))
                },
                navigationIcon = {
                    AutoSizeFade(
                        compact = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                ACIconSmall(ACIconDefault.DrawerOpen, null)
                            }
                        }
                    )
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
                                sharedVM.userVM.baseSearchVM.openSearchDrawer(
                                    getSearchHistory = Repository.local::getUserSearchHistory,
                                    setSearchHistory = Repository.local::setUserSearchHistory,
                                    onConfirm = sharedVM.userVM::refreshUpdateUserListReq
                                )
                                // 打开搜索抽屉后，获取搜索历史
                                sharedVM.userVM.baseSearchVM.getSearchHistory()
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
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
            // 外层横向滚动（包裹表头+内容）
            Row(modifier = Modifier.weight(1f)) {
                LazyRow(
                    state = rowState,
                    modifier = Modifier.weight(1f)
                ) {
                    item {
                        Column {
                            // 表头
                            Row(
                                modifier = Modifier.padding(bottom = 16.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        CardDefaults.shape
                                    )
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                UserTableColumn.entries.forEach { t ->
                                    Text(
                                        text = stringResource(t.text),
                                        modifier = Modifier.width(t.width),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }

                            // 内容列表
                            LazyColumn(state = columnState) {
                                if (list.itemSnapshotList.isEmpty()) {
                                    item {
                                        EmptyPlaceholder()
                                    }
                                } else {
                                    items(list.itemSnapshotList.items) { item ->
                                        Card(
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                SelectionContainer {
                                                    Text(
                                                        text = item.id.toString(),
                                                        modifier = Modifier.width(UserTableColumn.ID.width)
                                                    )
                                                }

                                                SelectionContainer {
                                                    Text(
                                                        text = item.email,
                                                        modifier = Modifier.width(UserTableColumn.EMAIL.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = Status.i18nByOrdinal(item.status),
                                                        modifier = Modifier.width(UserTableColumn.STATUS.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.role,
                                                        modifier = Modifier.width(UserTableColumn.ROLE.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.uuid,
                                                        modifier = Modifier.width(UserTableColumn.UUID.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = TimeUtils.toLocalDateString(
                                                            item.createdAt
                                                        ),
                                                        modifier = Modifier.width(UserTableColumn.CREATED_AT.width)
                                                    )
                                                }
                                                SelectionContainer {
                                                    Text(
                                                        text = item.avatar ?: "",
                                                        modifier = Modifier.width(UserTableColumn.AVATAR.width),
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )
                                                }
                                                Row(
                                                    modifier = Modifier.width(UserTableColumn.OPERATE.width),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    IconButton(onClick = {
                                                        sharedVM.userVM.initEditUser(
                                                            EditType.UPDATE,
                                                            item
                                                        )
                                                        navController.toEditUser()
                                                    }) {
                                                        ACIconSmall(ACIconDefault.Edit, null)
                                                    }
                                                    IconButton(onClick = {
                                                        sharedVM.dialogVM.openDialog(
                                                            title = { Text(stringResource(Res.string.delete_confirmation)) }) {
                                                            ACButtonError(onClick = {
                                                                scope.launch {
                                                                    Repository.remote.deleteUser(
                                                                        DeleteUserReq(item.id)
                                                                    )
                                                                        .onFailure { r ->
                                                                            sharedVM.dialogVM.openDialog(
                                                                                title = { Text(r.code.toString()) },
                                                                                text = { Text(r.message) }
                                                                            )
                                                                        }
                                                                        .onSuccess {
                                                                            sharedVM.dialogVM.closeDialog()
                                                                            list.refresh()
                                                                        }
                                                                }
                                                            }) {
                                                                Text(stringResource(Res.string.delete))
                                                            }
                                                        }
                                                    }) {
                                                        ACIconSmall(ACIconDefault.Trash, null)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                platform.VerticalScrollbar(
                    state = columnState,
                    modifier = Modifier.padding(start = 16.dp).fillMaxHeight()
                )

            }

            platform.HorizontalScrollbar(
                state = rowState,
                modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth()
            )
        }
        BaseSearchDrawer(sharedVM.userVM.baseSearchVM)
    }
}