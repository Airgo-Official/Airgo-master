package io.github.ppoonk.airgo_master.ui.sign.signin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.github.ppoonk.ac.ui.component.ACIcon
import io.github.ppoonk.ac.ui.component.ACIconDefault
import io.github.ppoonk.ac.ui.component.ACIconSmall
import io.github.ppoonk.ac.ui.component.ACPasswordVisibilityToggle
import io.github.ppoonk.ac.ui.component.ACTextError
import io.github.ppoonk.ac.ui.component.ACTextField
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.email
import io.github.ppoonk.airgo_master.logo
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.navigation.toMain
import io.github.ppoonk.airgo_master.password
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.sign_in
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen() {
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current
    val signInWidget by sharedVM.userVM.signInWidget.collectAsState()
    val securityWidget by sharedVM.configurationVM.securityWidget.collectAsState()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .imePadding(), // 避免软键盘遮挡
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 圆形图片
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .padding(bottom = 16.dp)
            )

            // 邮箱输入框
            ACTextField(
                value = signInWidget.email,
                label = { Text(stringResource(Res.string.email)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                onValueChange = { sharedVM.userVM.signInEmail(it) },
                leadingIcon = { ACIconSmall(ACIconDefault.User, null) },
                isError = signInWidget.emailError.isNotEmpty(),
                supportingText = { ACTextError(signInWidget.emailError) },
            )
            // 密码输入框
            ACTextField(
                value = signInWidget.password,
                label = { Text(stringResource(Res.string.password)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                onValueChange = { sharedVM.userVM.signInPassword(it) },
                isError = signInWidget.passwordError.isNotEmpty(),
                supportingText = { ACTextError(signInWidget.passwordError) },
                visualTransformation = if (signInWidget.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = { ACIconSmall(ACIconDefault.Lock, null) },
                trailingIcon = {
                    ACPasswordVisibilityToggle(
                        onClick = { sharedVM.userVM.signInShowPassword() },
                        isVisible = signInWidget.showPassword,
                    )
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable {
                    sharedVM.userVM.signInShowMore()
                },
            ) {
                Text("更多")
                ACIcon(
                    if (signInWidget.showMore) ACIconDefault.AngleUp else ACIconDefault.AngleDown,
                    null
                )
            }

            AnimatedVisibility(signInWidget.showMore) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // api 地址
                    ACTextField(
                        value = securityWidget.localApiUrl,
                        label = { Text("API 地址") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        onValueChange = { sharedVM.configurationVM.localApiUrl(it) },
                        leadingIcon = { ACIconSmall(ACIconDefault.Server, null) },
                    )
                    // 管理员安全路径
                    ACTextField(
                        value = securityWidget.localAdminPath,
                        label = { Text("管理员安全路径") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        onValueChange = { sharedVM.configurationVM.localAdminPath(it) },
                        leadingIcon = { ACIconSmall(ACIconDefault.Shield, null) },
                    )
                }
            }


            // 登录按钮
            Button(
                enabled = signInWidget.isValid,
                onClick = {
                    scope.launch {
                        sharedVM.userVM.doSignInReq()
                            .onFailure {
                                sharedVM.dialogVM.openDialog(
                                    title = { Text(it.code.toString()) },
                                    text = { Text(it.message) }
                                )
                            }
                            .onSuccess {
                                navController.toMain(Routes.SignIn)
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(Res.string.sign_in),
                )
            }
        }
    }
}
