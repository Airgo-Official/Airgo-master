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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import coil3.compose.AsyncImage
import io.github.ppoonk.ac.ui.component.ACButtonError
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACTopAppBar
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.confirm
import io.github.ppoonk.airgo_master.defaultAvatar
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.navigation.toSignIn
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.sign_out
import io.github.ppoonk.airgo_master.sign_out_confirmation
import io.github.ppoonk.airgo_master.update_password
import io.github.ppoonk.airgo_master.warning
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeScreen() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
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
                        ACIconSmall(ACIconDefault.AngleLeft, null)
                    }
                },
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it).padding(horizontal = 16.dp)
        ) {
            item {
                val sharedVM = LocalSharedVM.current
                val signedInUser by sharedVM.userVM.signedInUser.collectAsState()
                signedInUser?.let { user ->
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
                        modifier = Modifier.fillMaxWidth().background(
                            MaterialTheme.colorScheme.background,
                            CardDefaults.shape
                        ).padding(bottom = 16.dp)
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
            }
            item {
                Card {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp).fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ACIconSmall(ACIconDefault.Shield, null, modifier = Modifier.weight(1f))
                            Text(
                                stringResource(Res.string.update_password),
                                modifier = Modifier.weight(4f)
                            )
                            ACIconSmall(
                                ACIconDefault.AngleRight,
                                null,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        ACButtonError(
                            onClick = {
                                scope.launch {
                                    sharedVM.dialogVM.openDialog(
                                        title = { Text(stringResource(Res.string.warning)) },
                                        text = { Text(stringResource(Res.string.sign_out_confirmation)) },
                                    ) {
                                        ACButtonError(
                                            onClick = {
                                                scope.launch {
                                                    Repository.local.setToken(null)
                                                    sharedVM.dialogVM.closeDialog()
                                                    navController.toSignIn(Routes.Main)
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(horizontal = 16.dp),
                                            shape = CardDefaults.shape
                                        )
                                        {
                                            Text(stringResource(Res.string.confirm))
                                        }
                                    }
                                }
                            },
                            shape = CardDefaults.shape,
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                        ) {
                            ACIconSmall(ACIconDefault.SignOut, null)
                            Spacer(Modifier.width(20.dp))
                            Text(stringResource(Res.string.sign_out))
                        }
                    }
                }
            }
        }
    }
}



