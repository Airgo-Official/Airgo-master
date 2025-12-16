package io.github.ppoonk.airgo_master.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ppoonk.ac.utils.Logger
import io.github.ppoonk.ac.utils.TimeUtils
import io.github.ppoonk.airgo_master.LocalNavController
import io.github.ppoonk.airgo_master.LocalSharedVM
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.copyright
import io.github.ppoonk.airgo_master.logo
import io.github.ppoonk.airgo_master.navigation.Routes
import io.github.ppoonk.airgo_master.navigation.toMain
import io.github.ppoonk.airgo_master.navigation.toSignIn
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun SplashScreen(): Unit {
    val navController = LocalNavController.current
    val sharedVM = LocalSharedVM.current

    var next by remember { mutableStateOf<Routes>(Routes.Splash) }

    LaunchedEffect(Unit) {
        next = sharedVM.userVM.checkSigned()
    }
    when (next) {
        Routes.SignIn -> {
            Logger.debug(Logger.COMPOSE_UI) { "to SignIn" }
            navController.toSignIn(Routes.Splash)
        }

        Routes.Main -> {
            Logger.debug(Logger.COMPOSE_UI) { "to Main" }
            navController.toMain(Routes.Splash)
        }
        else -> {}
    }

    Box(
        modifier = Modifier.fillMaxSize()

    ) {
        Image(
            painterResource(Res.drawable.logo),
            null,
            modifier = Modifier
                .align(Alignment.Center)
        )
        Text(
            text = stringResource(
                Res.string.copyright,
                TimeUtils.getCurrentDateTime().year - 2,
                TimeUtils.getCurrentDateTime().year
            ),
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        )
    }
}