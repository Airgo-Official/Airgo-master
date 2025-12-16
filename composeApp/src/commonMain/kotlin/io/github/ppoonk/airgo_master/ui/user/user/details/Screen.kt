package io.github.ppoonk.airgo_master.ui.user.user.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import coil3.compose.AsyncImage
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACCard
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACLabelInfo
import io.github.ppoonk.ac.ui.component.ACLabelPrimary
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.ac.utils.TimeUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.defaultAvatar
import io.github.ppoonk.airgo_master.navigation.toEditUser
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.DeleteUserReq
import io.github.ppoonk.airgo_master.repository.remote.model.RoleConst
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.User
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource


@Composable
fun DetailUserScreen() {
    Scaffold(
        topBar = { DetailUserScreenTopBar() },
    ) {

        SelectionContainer(
            modifier = Modifier.padding(it)
        ) {
            val sharedVM = LocalSharedVM.current
            val currentUser by sharedVM.userVM.currentUser.collectAsState()

            LazyColumn {
                item {
                    currentUser?.let { user ->
                        UserInfoProfile(user)
                        UserInfoDetails(user)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailUserScreenTopBar(
) {
    val navController = LocalNavController.current
    val scope = rememberCoroutineScope()
    val sharedVM = LocalSharedVM.current
    val currentUser by sharedVM.userVM.currentUser.collectAsState()

    ACTopAppBar(
        title = {
            Text(
                text = "用户详情"
            )
        },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    navController.popBackStack()
                }
            }) {
                ACIconSmall(ACIconDefault.AngleLeft, null)
            }
        },
        actions = {
            // 更新
            IconButton(onClick = {
                scope.launch {
                    sharedVM.userVM.initEditUser(EditType.UPDATE)
                    navController.toEditUser()
                }
            }) {
                ACIconSmall(ACIconDefault.Edit,null)
            }
            // 删除
            IconButton(onClick = {
                sharedVM.dialogVM.openDialog(
                    title = { Text("提示") },
                    text = { Text("删除后数据无法恢复，确认删除吗?") }
                ) {
                    ACButtonError(
                        onClick = {
                            scope.launch {
                                Repository.remote.deleteUser(DeleteUserReq(id = currentUser!!.id))
                                    .onFailure {
                                        sharedVM.dialogVM.openDialog(
                                            title = { Text(it.code.toString()) },
                                            text = { Text(it.message) }
                                        )
                                    }
                                    .onSuccess {
                                        sharedVM.dialogVM.closeDialog()
                                        navController.popBackStack()
                                    }
                            }
                        },
                    ) {
                        Text("确认")
                    }
                }
            }) {
                ACIconSmall(ACIconDefault.Trash,null)
            }
        },
    )
}

@Composable
fun UserInfoProfile(user: User): Unit {
    val set = ConstraintSet {
        val (avatar, id, email) = createRefsFor(
            "avatar",
            "id",
            "email",
        )
        val startGuideline = createGuidelineFromStart(0.4f)
        createVerticalChain(id, email, chainStyle = ChainStyle.Spread)

        constrain(avatar) {
            centerVerticallyTo(parent)
            start.linkTo(parent.start, 16.dp)
        }
        constrain(id) {
            start.linkTo(startGuideline)
        }
        constrain(email) {
            start.linkTo(startGuideline)
        }
    }

    ConstraintLayout(
        constraintSet = set,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(
            MaterialTheme.colorScheme.background,
            RoundedCornerShape(12.dp)
        )
    ) {
        AsyncImage(
            user.avatar, null,
            modifier = Modifier
                .size(100.dp) // 设置图片大小为100dp
                .clip(CircleShape)
                .layoutId("avatar"),
            error = painterResource(Res.drawable.defaultAvatar),
            placeholder = painterResource(Res.drawable.defaultAvatar),
        )

        Text("ID: ${user.id}", modifier = Modifier.layoutId("id"))
        Text(
            user.email,
            modifier = Modifier.layoutId("email")
        )

    }
}

@Composable
fun UserInfoDetails(user: User): Unit {
    ACCard(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("状态")
                if (user.status == Status.ENABLE.ordinal) ACLabelPrimary("启用") else ACLabelInfo(
                    "禁用"
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("角色")
                ACLabelInfo(if (user.role == RoleConst.ADMIN.name) "管理员" else "普通用户")
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("创建时间")
                Text(TimeUtils.toLocalDateString(user.createdAt))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("UUID")
                Text(
                    user.uuid,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = MaterialTheme.typography.bodySmall.fontSize * 0.8f),
                )
            }
        }
    }

}