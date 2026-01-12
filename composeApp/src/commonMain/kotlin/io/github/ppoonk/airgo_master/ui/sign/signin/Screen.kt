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
import androidx.compose.material3.OutlinedTextField
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
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.configuration_api_base_url
import io.github.ppoonk.airgo_master.configuration_api_base_url_placeholder
import io.github.ppoonk.airgo_master.configuration_backend_admin_path
import io.github.ppoonk.airgo_master.configuration_backend_admin_path_placeholder
import io.github.ppoonk.airgo_master.email
import io.github.ppoonk.airgo_master.logo
import io.github.ppoonk.airgo_master.more
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.navigation.toMain
import io.github.ppoonk.airgo_master.password
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
    val securityWidget by sharedVM.configurationVM.editSecurityWidget.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 600.dp)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
                .imePadding(),
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
            OutlinedTextField(
                value = signInWidget.email,
                label = { Text(stringResource(Res.string.email)) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                onValueChange = { sharedVM.userVM.signInEmail(it) },
                leadingIcon = { ACIconSmall(ACIconDefault.User, null) },
                isError = signInWidget.emailError.isNotEmpty(),
                supportingText = { ACTextError(signInWidget.emailError) },
            )
            // 密码输入框
            OutlinedTextField(
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
                Text(stringResource(Res.string.more))
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
                    OutlinedTextField(
                        value = securityWidget.localApiUrl,
                        label = { Text(stringResource(Res.string.configuration_api_base_url)) },
                        placeholder = { Text(stringResource(Res.string.configuration_api_base_url_placeholder)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        onValueChange = { sharedVM.configurationVM.localApiUrl(it) },
                        leadingIcon = { ACIconSmall(ACIconDefault.Server, null) },
                    )
                    // 管理员安全路径
                    OutlinedTextField(
                        value = securityWidget.localAdminPath,
                        label = { Text(stringResource(Res.string.configuration_backend_admin_path)) },
                        placeholder = { Text(stringResource(Res.string.configuration_backend_admin_path_placeholder)) },
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
