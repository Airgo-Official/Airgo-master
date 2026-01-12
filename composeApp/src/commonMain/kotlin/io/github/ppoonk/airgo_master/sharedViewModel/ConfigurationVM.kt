package io.github.ppoonk.airgo_master.sharedViewModel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import io.github.ppoonk.ac.utils.ValidationResult
import io.github.ppoonk.ac.utils.ValidationUtils
import io.github.ppoonk.ac.utils.onFailure
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.airgo_master.Res
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.configuration_log_status_recording
import io.github.ppoonk.airgo_master.configuration_log_status_stopped
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.AlipayConfig
import io.github.ppoonk.airgo_master.repository.remote.model.EmailConfig
import io.github.ppoonk.airgo_master.repository.remote.model.EpayConfig
import io.github.ppoonk.airgo_master.repository.remote.model.Payment
import io.github.ppoonk.airgo_master.repository.remote.model.PaymentType
import io.github.ppoonk.airgo_master.repository.remote.model.Push
import io.github.ppoonk.airgo_master.repository.remote.model.PushType
import io.github.ppoonk.airgo_master.repository.remote.model.Security
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.StripeConfig
import io.github.ppoonk.airgo_master.repository.remote.model.TgBotConfig
import io.github.ppoonk.airgo_master.repository.remote.model.TronConfig
import io.github.ppoonk.airgo_master.repository.remote.model.TronToken
import io.github.ppoonk.airgo_master.repository.remote.model.UpdatePaymentListReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdatePushListReq
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.jetbrains.compose.resources.stringResource

class ConfigurationVM : ViewModel() {
    private val _currentPayment = MutableStateFlow<Payment?>(null)

    private val _paymentList = MutableStateFlow<List<Payment>>(emptyList())
    val paymentList: StateFlow<List<Payment>> = _paymentList
    suspend fun getPaymentList(): Unit {
        Repository.remote.getPaymentList()
            .onSuccess { r ->
                r.data?.list?.let {
                    _paymentList.value = it
                }
            }
    }

    private val _editPaymentWidget = MutableStateFlow(EditPaymentWidget())
    val editPaymentWidget: StateFlow<EditPaymentWidget> = _editPaymentWidget


    fun initEditPayment(editType: EditType, current: Payment? = null): Unit {
        when (editType) {
            EditType.CREATE -> _editPaymentWidget.value = EditPaymentWidget()

            EditType.UPDATE -> {

                current?.let { p ->
                    _currentPayment.value = current
                    _editPaymentWidget.value = EditPaymentWidget(
                        name = p.name,
                        status = Status.entries[p.status],
                        paymentType = PaymentType.valueOf(p.paymentType.uppercase()),
                        alipayConfig = p.alipayConfig ?: AlipayConfig(),
                        epayConfig = p.epayConfig ?: EpayConfig(),
                        stripeConfig = p.stripeConfig ?: StripeConfig(),
                        tronConfig = p.tronConfig ?: TronConfig(),
                        editType = editType,
                        paymentList = _paymentList.value.filter { it.name != p.name }  // 先移除当前支付，点击更新按钮时再添加到列表
                    )
                }
            }
        }
    }

    fun paymentName(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }
        if (err.isNotEmpty()) {
            _editPaymentWidget.value = _editPaymentWidget.value.copy(name = v, nameError = err)
            return
        }

        val isExist = when (_editPaymentWidget.value.editType) {
            EditType.CREATE -> _paymentList.value.any { it.name == v }
            EditType.UPDATE -> _editPaymentWidget.value.paymentList.any { it.name == v }
        }
        val nameError = if (isExist) "该名称已存在" else ""
        _editPaymentWidget.value = _editPaymentWidget.value.copy(name = v, nameError = nameError)
    }

    fun paymentStatus(v: Boolean): Unit {
        _editPaymentWidget.value =
            _editPaymentWidget.value.copy(status = if (v) Status.ENABLE else Status.DISABLE)
    }

    fun paymentTypeExpanded(): Unit {
        _editPaymentWidget.value =
            _editPaymentWidget.value.copy(paymentTypeExpanded = !_editPaymentWidget.value.paymentTypeExpanded)
    }

    fun paymentType(v: PaymentType): Unit {
        _editPaymentWidget.value =
            _editPaymentWidget.value.copy(paymentType = v, paymentTypeExpanded = false)
    }

    fun alipayConfig(v: AlipayConfig): Unit {
        _editPaymentWidget.value = _editPaymentWidget.value.copy(alipayConfig = v)
    }

    fun epayConfig(v: EpayConfig): Unit {
        _editPaymentWidget.value = _editPaymentWidget.value.copy(epayConfig = v)
    }

    fun stripeConfig(v: StripeConfig): Unit {
        _editPaymentWidget.value = _editPaymentWidget.value.copy(stripeConfig = v)
    }

    fun tronConfig(v: TronConfig): Unit {
        _editPaymentWidget.value = _editPaymentWidget.value.copy(tronConfig = v)
    }

    fun tronToken(v: TronToken, isAdd: Boolean): Unit {
        val tokens = _editPaymentWidget.value.tronConfig.acceptTokens.toMutableList()
        if (isAdd) {
            tokens.add(v.name)
        } else {
            tokens.remove(v.name)
        }
        _editPaymentWidget.value = _editPaymentWidget.value.copy(
            tronConfig = _editPaymentWidget.value.tronConfig.copy(acceptTokens = tokens.toList())
        )
    }


    fun toUpdatePaymentReq(): UpdatePaymentListReq {
        val updatedList = when (_editPaymentWidget.value.editType) {
            EditType.CREATE -> _paymentList.value.plus(_editPaymentWidget.value.toPayment())

            EditType.UPDATE -> _editPaymentWidget.value.paymentList.plus(_editPaymentWidget.value.toPayment())
        }
        return UpdatePaymentListReq(updatedList)
    }

    fun toDeletePaymentReq(): UpdatePaymentListReq {
        return UpdatePaymentListReq(_editPaymentWidget.value.paymentList)
    }


    suspend fun getSecurity(): Unit {
        Repository.remote.getSecurity()
            .onFailure { }
            .onSuccess { r ->
                r.data?.let {
                    _editSecurityWidget.value = _editSecurityWidget.value.copy(
                        adminPath = it.adminPath,
                        tokenSign = it.tokenSign,
                        tokenDuration = it.tokenDuration.toString()
                    )
                }
            }
    }

    private val _editSecurityWidget = MutableStateFlow<EditSecurityWidget>(EditSecurityWidget())
    val editSecurityWidget: StateFlow<EditSecurityWidget> = _editSecurityWidget


    fun localApiUrl(v: String): Unit {
        _editSecurityWidget.value = _editSecurityWidget.value.copy(localApiUrl = v)
        Repository.local.setBaseUrl(v)
    }

    fun localAdminPath(v: String): Unit {
        _editSecurityWidget.value = _editSecurityWidget.value.copy(localAdminPath = v)
        Repository.local.setAdminPath(v)
    }

    fun adminPath(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }
        _editSecurityWidget.value = _editSecurityWidget.value.copy(
            adminPath = v,
            adminPathError = err
        )
    }

    fun tokenSign(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }

        _editSecurityWidget.value = _editSecurityWidget.value.copy(
            tokenSign = v,
            tokenSignError = err
        )
    }

    fun tokenDuration(v: String): Unit {
        val err =
            when (val r = ValidationUtils.validateNumber(v, 60..3600 * 720 * 365)) { // 60秒 ～ 1年
                is ValidationResult.Failure -> r.error
                is ValidationResult.Success -> ""
            }
        _editSecurityWidget.value = _editSecurityWidget.value.copy(
            tokenDuration = v,
            tokenDurationError = err
        )
    }


    fun initEditPush(editType: EditType, p: Push? = null): Unit {
        when (editType) {
            EditType.CREATE -> _editPushWidget.value = EditPushWidget()

            EditType.UPDATE -> {
                _currentPush.value = p
                p?.let { p ->
                    _editPushWidget.value = EditPushWidget(
                        name = p.name,
                        status = Status.entries[p.status],
                        pushType = PushType.valueOf(p.pushType),
                        emailConfig = p.emailConfig ?: EmailConfig(),
                        tgBotConfig = p.tgBotConfig ?: TgBotConfig(),
                        editType = editType,
                        pushList = _pushList.value.filter { it.name != p.name }
                    )
                }
            }
        }
    }

    private val _currentPush = MutableStateFlow<Push?>(null)

    private val _pushList = MutableStateFlow<List<Push>>(emptyList())
    val pushList: StateFlow<List<Push>> = _pushList
    suspend fun getPushList(): Unit {
        Repository.remote.getPushList()
            .onFailure { }
            .onSuccess { r ->
                r.data?.list?.let {
                    _pushList.value = it
                }
            }
    }

    private val _editPushWidget = MutableStateFlow<EditPushWidget>(EditPushWidget())
    val editPushWidget: StateFlow<EditPushWidget> = _editPushWidget

    fun refreshPushName(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }
        if (err.isNotEmpty()) {
            _editPushWidget.value = _editPushWidget.value.copy(name = v, nameError = err)
            return
        }

        val isExist = when (_editPushWidget.value.editType) {
            EditType.CREATE -> _pushList.value.any { it.name == v }
            EditType.UPDATE -> _editPushWidget.value.pushList.any { it.name == v }
        }
        val nameError = if (isExist) "该名称已存在" else ""
        _editPushWidget.value = _editPushWidget.value.copy(name = v, nameError = nameError)
    }

    fun pushStatus(v: Boolean): Unit {
        _editPushWidget.value =
            _editPushWidget.value.copy(status = if (v) Status.ENABLE else Status.DISABLE)
    }

    fun expandPushType(): Unit {
        _editPushWidget.value =
            _editPushWidget.value.copy(expandPushType = !_editPushWidget.value.expandPushType)
    }

    fun pushType(v: PushType): Unit {
        _editPushWidget.value =
            _editPushWidget.value.copy(pushType = v, expandPushType = false)
    }

    fun tgBotConfig(v: TgBotConfig): Unit {
        _editPushWidget.value = _editPushWidget.value.copy(tgBotConfig = v)
    }

    fun emailConfig(v: EmailConfig): Unit {
        _editPushWidget.value = _editPushWidget.value.copy(emailConfig = v)
    }

    fun toUpdatePushReq(): UpdatePushListReq {
        val updatedList = when (_editPushWidget.value.editType) {
            EditType.CREATE -> _pushList.value.plus(_editPushWidget.value.toPush())

            EditType.UPDATE -> _editPushWidget.value.pushList.plus(_editPushWidget.value.toPush())
        }
        return UpdatePushListReq(updatedList)
    }

    fun toDeletePushReq(): UpdatePushListReq {
        return UpdatePushListReq(_editPushWidget.value.pushList)
    }


    //日志
    private val _logList = MutableStateFlow<List<String>>(emptyList())
    val logList: StateFlow<List<String>> = _logList

    private val _logStatus = MutableStateFlow<LogStatus>(LogStatus.STOPPED)
    val logStatus: StateFlow<LogStatus> = _logStatus

    fun logStatus(v: LogStatus): Unit {
        _logStatus.value = v
    }

    fun addLog(log: String): Unit {
        _logList.update {
            (it + log).takeLast(1000) // 保持最新1000条
        }
    }

    fun clearLog(): Unit {
        _logList.value = emptyList()
    }


}

data class EditSecurityWidget(
    // 登录时设置的
    val localApiUrl: String = Repository.local.getBaseUrl() ?: "",
    val localAdminPath: String = Repository.local.getAdminPath() ?: "",

    val adminPath: String = "",
    val tokenSign: String = "",
    val tokenDuration: String = "",

    val adminPathError: String = "",
    val tokenSignError: String = "",
    val tokenDurationError: String = ""
) {
    val isUpdateValid: Boolean
        get() = adminPathError.isEmpty() &&
                tokenSignError.isEmpty() &&
                tokenDurationError.isEmpty()

    fun toSecurity(): Security {
        return Security(
            adminPath = this.adminPath,
            tokenSign = this.tokenSign,
            tokenDuration = this.tokenDuration.toInt()
        )
    }
}


data class EditPaymentWidget(
    val id: UInt = 0u,
    val name: String = "",
    val status: Status = Status.ENABLE,
    val paymentType: PaymentType = PaymentType.ALIPAY,
    val alipayConfig: AlipayConfig = AlipayConfig(),
    val epayConfig: EpayConfig = EpayConfig(),
    val stripeConfig: StripeConfig = StripeConfig(),
    val tronConfig: TronConfig = TronConfig(),

    val editType: EditType = EditType.CREATE,
    val paymentList: List<Payment> = emptyList(),
    val nameError: String = "",

    val paymentTypeExpanded: Boolean = false,

    ) {
    val createIsValid: Boolean
        get() = name.isNotEmpty() &&
                when (paymentType) {
                    PaymentType.ALIPAY -> alipayConfig.isValid
                    PaymentType.EPAY -> epayConfig.isValid
                    PaymentType.STRIPE -> stripeConfig.isValid
                    PaymentType.TRON -> tronConfig.isValid
                }

    val updateIsValid: Boolean // TODO 优化
        get() = id > 0u


    fun toPayment(): Payment {
        var p = Payment(
            name = name,
            status = status.ordinal,
            paymentType = paymentType.name,
            alipayConfig = null,
            epayConfig = null,
            stripeConfig = null,
            tronConfig = null,
        )
        p = when (paymentType) {
            PaymentType.ALIPAY -> p.copy(alipayConfig = alipayConfig)
            PaymentType.EPAY -> p.copy(epayConfig = epayConfig)
            PaymentType.STRIPE -> p.copy(stripeConfig = stripeConfig)
            PaymentType.TRON -> p.copy(tronConfig = tronConfig)
        }
        return p
    }
}

data class EditPushWidget(
    val name: String = "",
    val status: Status = Status.ENABLE,
    val pushType: PushType = PushType.EMAIL,
    val emailConfig: EmailConfig = EmailConfig(),
    val tgBotConfig: TgBotConfig = TgBotConfig(),

    val nameError: String = "",
    val expandPushType: Boolean = false,

    val pushList: List<Push> = emptyList(),
    val editType: EditType = EditType.CREATE,

    ) {
    fun toPush(): Push {
        var p = Push(
            name = name,
            status = status.ordinal,
            pushType = pushType.name,
            emailConfig = null,
            tgBotConfig = null
        )
        p = when (pushType) {
            PushType.TG_BOT -> p.copy(tgBotConfig = tgBotConfig)
            PushType.EMAIL -> p.copy(emailConfig = emailConfig)
        }
        return p
    }
}
enum class LogStatus{
    STOPPED,
    RECORDING;
    @Composable
    fun i18n(): String {
        return when (this) {
            STOPPED -> stringResource(Res.string.configuration_log_status_stopped)
            RECORDING -> stringResource(Res.string.configuration_log_status_recording)
        }
    }
}


