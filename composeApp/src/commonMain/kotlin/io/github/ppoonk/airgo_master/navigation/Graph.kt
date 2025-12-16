package io.github.ppoonk.airgo_master.navigation

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.ppoonk.airgo_master.ui.configuration.payment.edit.EditPaymentScreen
import io.github.ppoonk.airgo_master.ui.configuration.push.edit.EditPushScreen
import io.github.ppoonk.airgo_master.ui.main.MainScreen
import io.github.ppoonk.airgo_master.ui.node.node.detail.NodeDetailsScreen
import io.github.ppoonk.airgo_master.ui.node.node.edit.EditNodeScreen
import io.github.ppoonk.airgo_master.ui.node.protocol.edit.EditProtocolScreen
import io.github.ppoonk.airgo_master.ui.node.protocolTemplate.edit.EditProtocolTemplateScreen
import io.github.ppoonk.airgo_master.ui.persion.me.MeScreen
import io.github.ppoonk.airgo_master.ui.sign.signin.SignInScreen
import io.github.ppoonk.airgo_master.ui.splash.SplashScreen
import io.github.ppoonk.airgo_master.ui.store.coupon.edit.EditCouponScreen
import io.github.ppoonk.airgo_master.ui.store.product.edit.EditProductScreen
import io.github.ppoonk.airgo_master.ui.user.user.details.DetailUserScreen
import io.github.ppoonk.airgo_master.ui.user.user.edit.EditUserScreen
import kotlinx.serialization.Serializable

sealed class Routes {
    @Serializable
    data object Splash : Routes()

    @Serializable
    data object SignIn : Routes()

    @Serializable
    data object Main : Routes()

    @Serializable
    data object EditNode : Routes()

    @Serializable
    data object DetailNode : Routes()

    @Serializable
    data object EditProtocol : Routes()

    @Serializable
    data object EditProtocolTemplate : Routes()

    @Serializable
    data object Me : Routes()

    @Serializable
    data object EditProduct : Routes()

    @Serializable
    data object EditCoupon : Routes()

    @Serializable
    data object EditPayment : Routes()

    @Serializable
    data object EditPush : Routes()

    @Serializable
    data object EditUser : Routes()

    @Serializable
    data object DetailUser : Routes()
}

fun NavController.toSignIn(popUpTo: Routes) = navigate(Routes.SignIn) {
    launchSingleTop = true
    popUpTo(popUpTo) {
        inclusive = true
    }
}

fun NavController.toMain(popUpTo: Routes) = navigate(Routes.Main) {
    launchSingleTop = true
    popUpTo(popUpTo) {
        inclusive = true
    }
}

fun NavController.toEditNode() = navigate(Routes.EditNode) { launchSingleTop = true }

fun NavController.toNodeDetails() = navigate(Routes.DetailNode) { launchSingleTop = true }

fun NavController.toEditProtocol() = navigate(Routes.EditProtocol) { launchSingleTop = true }

fun NavController.toEditProtocolTemplate() = navigate(Routes.EditProtocolTemplate) { launchSingleTop = true }

fun NavController.toMe() = navigate(Routes.Me) { launchSingleTop = true }


fun NavController.toEditProduct() = navigate(Routes.EditProduct) { launchSingleTop = true }

fun NavController.toEditCoupon() = navigate(Routes.EditCoupon) { launchSingleTop = true }


fun NavController.toEditPayment() = navigate(Routes.EditPayment) { launchSingleTop = true }

fun NavController.toEditPush() = navigate(Routes.EditPush) { launchSingleTop = true }


fun NavController.toEditUser() = navigate(Routes.EditUser) { launchSingleTop = true }

fun NavController.toDetailUser() = navigate(Routes.DetailUser) { launchSingleTop = true }

@OptIn(ExperimentalLayoutApi::class)
fun NavGraphBuilder.allGraph(): Unit {

    composable<Routes.Splash> { SplashScreen() }

    composable<Routes.SignIn> { SignInScreen() }

    composable<Routes.Main> { MainScreen() }

    composable<Routes.EditNode> { EditNodeScreen() }

    composable<Routes.DetailNode> { NodeDetailsScreen() }

    composable<Routes.EditProtocol> { EditProtocolScreen() }

    composable<Routes.EditProtocolTemplate> { EditProtocolTemplateScreen() }

    composable<Routes.Me> { MeScreen() }

    composable<Routes.EditProduct> { EditProductScreen() }

    composable<Routes.EditCoupon> { EditCouponScreen() }

    composable<Routes.EditPayment> { EditPaymentScreen() }

    composable<Routes.EditPush> { EditPushScreen() }

    composable<Routes.EditUser> { EditUserScreen() }

    composable<Routes.DetailUser> { DetailUserScreen() }
}
