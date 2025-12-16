package io.github.ppoonk.airgo_master

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import io.github.ppoonk.ac.ui.component.ACAppRoute
import io.github.ppoonk.ac.ui.component.ACSnackbar
import io.github.ppoonk.ac.ui.theme.ACTheme
import io.github.ppoonk.ac.utils.ApiHttpClient
import io.github.ppoonk.ac.utils.DefaultHandleHttpStatus
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.ac.utils.Result
import io.github.ppoonk.airgo_master.component.GlobalComponent
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.navigation.allGraph
import io.github.ppoonk.airgo_master.navigation.toSignIn
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.ApiService
import io.github.ppoonk.airgo_master.sharedViewModel.SharedVM
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch

val LocalNavController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided!")
}
val LocalSharedVM = staticCompositionLocalOf<SharedVM> {
    error("SharedViewModel not provided")
}
val LocalDrawerState = staticCompositionLocalOf<DrawerState> {
    error("LocalDrawerState not provided")
}

@Composable
fun App() {
    ACTheme {
        val navController: NavHostController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val snackbarHostState = remember { SnackbarHostState() }
        val sharedVM = SharedVM()
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            // 初始化日志
            Logger.apply {
                enable(true)
                setMinSeverity("debug")
            }
            // 初始化 http client
            ApiService.apply {
                setHttpClient(
                    ApiHttpClient(
                        requestObserver = { r ->
                            Logger.debug(Logger.API_HTTP_CLIENT) { "request: $r" }
                            null
                        },
                        responseObserver = { r ->
                            scope.launch {
                                val body = r.body<String>()
                                Logger.debug(Logger.API_HTTP_CLIENT) { "response status: ${r.status}, body: $body" }
                            }
                            // token 缺失、过期或格式不正确
                            if (r.status == HttpStatusCode.Unauthorized) {
                                sharedVM.snackbarVM.openSnackbar("Token error, sign in again")
                                navController.toSignIn(Routes.Main)
                                return@ApiHttpClient Result.Error(
                                    code = r.status.value,
                                    "Token error, sign in again"
                                )
                            }
                            // 其他情况默认处理
                            val err = DefaultHandleHttpStatus(r)
                            if (err != null) {
                                sharedVM.snackbarVM.openSnackbar(err.message)
                            }
                            err
                        }
                    )
                )
                setBaseUrl { Repository.local.getBaseUrl() ?: "" }
                setAdminPath { Repository.local.getAdminPath() ?: "" }
                setToken {
                    val t = Repository.local.getToken()
                    if (t.isNullOrEmpty()) "" else {
                        if (t.startsWith("Bearer ")) t else "Bearer $t"
                    }
                }
            }
        }

        // 初始化 LocalProvider
        CompositionLocalProvider(
            LocalNavController provides navController,
            LocalDrawerState provides drawerState,
            LocalSharedVM provides sharedVM,
        ) {
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        snackbar = { ACSnackbar(it) }
                    )
                },
            ) {
                // 页面导航，起始页：Routes.Splash
                ACAppRoute(navController = navController, startDestination = Routes.Splash) {
                    allGraph()
                }
                // 全局提示组件
                GlobalComponent(snackbarHostState = snackbarHostState, sharedVM = sharedVM)
            }
        }

    }
}

