package io.github.ppoonk.airgo_master.sharedViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import io.github.ppoonk.ac.utils.Result
import io.github.ppoonk.ac.utils.ValidationResult
import io.github.ppoonk.ac.utils.ValidationUtils
import io.github.ppoonk.ac.utils.onSuccess
import io.github.ppoonk.ac.utils.onlyNumberIfEmptyZero
import io.github.ppoonk.airgo_master.component.BaseSearchWidget
import io.github.ppoonk.airgo_master.component.EditType
import io.github.ppoonk.airgo_master.component.SearchType
import io.github.ppoonk.airgo_master.repository.Repository
import io.github.ppoonk.airgo_master.repository.remote.model.CreateNodeReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateProtocolReq
import io.github.ppoonk.airgo_master.repository.remote.model.CreateProtocolTemplateReq
import io.github.ppoonk.airgo_master.repository.remote.model.FilterNode
import io.github.ppoonk.airgo_master.repository.remote.model.GetNodeListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetProtocolListReq
import io.github.ppoonk.airgo_master.repository.remote.model.GetProtocolTemplateListReq
import io.github.ppoonk.airgo_master.repository.remote.model.Node
import io.github.ppoonk.airgo_master.repository.remote.model.NodeConst
import io.github.ppoonk.airgo_master.repository.remote.model.Protocol
import io.github.ppoonk.airgo_master.repository.remote.model.ProtocolTemplate
import io.github.ppoonk.airgo_master.repository.remote.model.SearchNode
import io.github.ppoonk.airgo_master.repository.remote.model.SearchProtocol
import io.github.ppoonk.airgo_master.repository.remote.model.Status
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateNodeReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateProtocolReq
import io.github.ppoonk.airgo_master.repository.remote.model.UpdateProtocolTemplateReq
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest

class NodeVM() : ViewModel() {
    // 协议模板
    private val _getProtocolTemplateListReq = MutableStateFlow(GetProtocolTemplateListReq())

    @OptIn(ExperimentalCoroutinesApi::class)
    val protocolTemplateList: Flow<PagingData<ProtocolTemplate>> =
        _getProtocolTemplateListReq.flatMapLatest {
            Repository.newPagingSource { p ->
                when (val res = Repository.remote.getProtocolTemplateList(
                    it.copy(
                        pagination = it.pagination.copy(
                            page = p.first,
                            pageSize = p.second
                        )
                    )
                )) {
                    is Result.Error -> Pair(emptyList(), 0)
                    is Result.Success -> Pair(res.data!!.list, res.data!!.total)
                }
            }.flow.cachedIn(viewModelScope)
        }


    private val _editProtocolTemplateWidget = MutableStateFlow(EditProtocolTemplateWidget())
    val editProtocolTemplateWidget: StateFlow<EditProtocolTemplateWidget> =
        _editProtocolTemplateWidget

    fun initEditProtocolTemplate(editType: EditType, current: ProtocolTemplate? = null): Unit {
        when (editType) {
            EditType.CREATE -> _editProtocolTemplateWidget.value = EditProtocolTemplateWidget()
            EditType.UPDATE -> {
                current?.let {
                    _editProtocolTemplateWidget.value = EditProtocolTemplateWidget(
                        id = it.id,
                        name = it.name,
                        oldUpdateProtocolTemplateReq = UpdateProtocolTemplateReq(
                            id = it.id,
                            name = it.name,
                            inbounds = it.inbounds
                        ),
                        editType = editType
                    )
                }
            }
        }
    }

    fun protocolTemplateName(v: String): Unit {
        _editProtocolTemplateWidget.value = _editProtocolTemplateWidget.value.copy(name = v)
    }

    fun protocolTemplateInbounds(v: String): Unit {
        val err = when (val r = ValidationUtils.validateJson(v)) {
            is ValidationResult.Failure -> r.error
            is ValidationResult.Success -> ""
        }
        _editProtocolTemplateWidget.value =
            _editProtocolTemplateWidget.value.copy(inbounds = v, inboundsError = err)
    }


    // 协议

    suspend fun getCurrentProductProtocolList(productId: UInt): List<UInt> {
        var result: List<Protocol> = emptyList()
        Repository.remote.getProtocolList(
            GetProtocolListReq(search = SearchProtocol(productId = productId))
        ).onSuccess { response ->
            result = response.data?.list ?: emptyList()
        }
        return result.map { it.id }
    }


    private val _currentNodeProtocolList = MutableStateFlow<List<Protocol>>(emptyList())
    val currentNodeProtocolList: StateFlow<List<Protocol>> = _currentNodeProtocolList
    suspend fun getCurrentNodeProtocolList(): Unit {
        Repository.remote.getProtocolList(GetProtocolListReq(search = SearchProtocol(nodeId = _currentNode.value?.id)))
            .onSuccess {
                _currentNodeProtocolList.value = it.data?.list ?: emptyList()
            }
    }


    private val _getProtocolListReq = MutableStateFlow(GetProtocolListReq())

    @OptIn(ExperimentalCoroutinesApi::class)
    val protocolList = _getProtocolListReq.flatMapLatest {
        Repository.newPagingSource { p ->
            when (val res = Repository.remote.getProtocolList(
                it.copy(
                    pagination = it.pagination.copy(
                        page = p.first,
                        pageSize = p.second
                    )
                )
            )) {
                is Result.Error -> Pair(emptyList(), 0)
                is Result.Success -> Pair(res.data?.list ?: emptyList(), res.data?.total ?: 0)
            }
        }.flow.cachedIn(viewModelScope)
    }

    private val _editProtocolWidget = MutableStateFlow(EditProtocolWidget())
    val editProtocolWidget: StateFlow<EditProtocolWidget> = _editProtocolWidget

    fun initEditProtocol(editType: EditType, current: Protocol? = null): Unit {
        when (editType) {
            EditType.CREATE -> _editProtocolWidget.value = EditProtocolWidget()
            EditType.UPDATE -> {
                current?.let {
                    _editProtocolWidget.value = _editProtocolWidget.value.copy(
                        id = it.id,
                        name = it.name,
                        templateId = it.templateId,
                        status = Status.entries[it.status],
                        address = it.address,
                        inbounds = it.inbounds,
                        port = it.port,
                        oldUpdateProtocolReq = UpdateProtocolReq(
                            id = it.id,
                            name = it.name,
                            templateId = it.templateId,
                            status = it.status,
                            address = it.address,
                            inbounds = it.inbounds,
                            port = it.port,
                        ),
                        editType = editType
                    )
                }
            }
        }


    }


    fun protocolNodeId(v: UInt): Unit {
        _editProtocolWidget.value = _editProtocolWidget.value.copy(nodeId = v)
    }

    fun protocolName(v: String): Unit {
        _editProtocolWidget.value = _editProtocolWidget.value.copy(name = v)
    }

    fun protocolAddress(v: String): Unit {
        _editProtocolWidget.value = _editProtocolWidget.value.copy(address = v)
    }

    fun protocolPort(v: String): Unit {
        _editProtocolWidget.value = _editProtocolWidget.value.copy(
            port = v.onlyNumberIfEmptyZero().toInt().coerceIn(1, 65535)
        )
    }

    fun expandedBind(): Unit {
        _editProtocolWidget.value =
            _editProtocolWidget.value.copy(expandedBind = !_editProtocolWidget.value.expandedBind)
    }

    fun protocolNoTemp(): Unit {
        _editProtocolWidget.value = _editProtocolWidget.value.copy(
            templateId = null,
            expandedBind = !_editProtocolWidget.value.expandedBind,
            selectedTemplateName = "不使用模板" // TODO
        )
    }

    fun protocolTemp(v: ProtocolTemplate): Unit {
        _editProtocolWidget.value = _editProtocolWidget.value.copy(
            templateId = v.id,
            inbounds = v.inbounds,
            expandedBind = !_editProtocolWidget.value.expandedBind,
            selectedTemplateName = v.name
        )
    }

    fun protocolInbounds(v: String): Unit {
        when (val r = ValidationUtils.validateJson(v)) {
            is ValidationResult.Failure -> {
                _editProtocolWidget.value = _editProtocolWidget.value.copy(
                    inbounds = v,
                    inboundsError = r.error
                )
            }

            is ValidationResult.Success -> {
                _editProtocolWidget.value = _editProtocolWidget.value.copy(
                    inbounds = r.res,
                    inboundsError = ""
                )
            }
        }
    }


    // 节点

    private val _currentNode = MutableStateFlow<Node?>(null)
    val currentNode: StateFlow<Node?> = _currentNode
    fun refreshCurrentNode(new: Node): Unit {
        _currentNode.value = new
    }

    private val _getNodeListReq = MutableStateFlow(GetNodeListReq())

    @OptIn(ExperimentalCoroutinesApi::class)
    val nodeList = _getNodeListReq.flatMapLatest {
        Repository.newPagingSource { p ->
            when (val res = Repository.remote.getNodeList(
                it.copy(
                    pagination = it.pagination.copy(
                        page = p.first,
                        pageSize = p.second
                    )
                )
            )) {
                is Result.Error -> Pair(emptyList(), 0)
                is Result.Success -> Pair(res.data?.list ?: emptyList(), res.data?.total ?: 0)
            }
        }.flow.cachedIn(viewModelScope)
    }

    fun refreshUpdateNodeListReq(v: BaseSearchWidget): Unit {
        with(v) {
            var req = GetNodeListReq()

            // search 参数
            search.isNotEmpty().let {
                val r1 = when (searchType) {
                    SearchType.ID -> SearchNode(id = search.toUIntOrNull())
                    SearchType.NAME -> SearchNode(name = search)
                }
                req = req.copy(search = r1)
            }

            // filter 参数
            val r2 = FilterNode(
                status = if (status == Status.ALL) null else status.ordinal,
                createdAtStart = datePickerStart,
                createdAtEnd = datePickerEnd
            )
            req = req.copy(filter = r2)
            _getNodeListReq.value = req
        }
    }

    private val _editNodeWidget = MutableStateFlow(EditNodeWidget())
    val editNodeWidget: StateFlow<EditNodeWidget> = _editNodeWidget

    fun initEditNode(editType: EditType, current: Node? = null): Unit {
        when (editType) {
            EditType.CREATE -> _editNodeWidget.value = EditNodeWidget()
            EditType.UPDATE -> {
                current?.let {
                    _editNodeWidget.value = EditNodeWidget(
                        id = it.id,
                        name = it.name,
                        status = Status.entries[it.status],
                        config = it.config,

                        oldUpdateNodeReq = UpdateNodeReq(
                            id = it.id,
                            name = it.name,
                            status = it.status,
                            config = it.config
                        ),
                        editType = editType,
                    )
                    _currentNode.value = current
                }
            }
        }

    }

    fun nodeName(v: String): Unit {
        val err = when (val r = ValidationUtils.validateEmpty(v)) {
            is ValidationResult.Failure -> r.error

            is ValidationResult.Success -> ""
        }
        _editNodeWidget.value = _editNodeWidget.value.copy(name = v, nameError = err)
    }

    fun nodeStatus(v: Boolean): Unit {
        _editNodeWidget.value =
            _editNodeWidget.value.copy(status = if (v) Status.ENABLE else Status.DISABLE)
    }

    fun nodeConfig(v: String): Unit {
        var err = ""
        var c = ""
        when (val r = ValidationUtils.validateJson(v)) {
            is ValidationResult.Failure -> {
                err = r.error
                c = v
            }

            is ValidationResult.Success -> {
                err = ""
                c = r.res
            }
        }
        _editNodeWidget.value = _editNodeWidget.value.copy(config = v, configError = err)
    }

}

data class EditProtocolTemplateWidget(
    val id: UInt = 0u,
    val name: String = "",
    val inbounds: String = NodeConst.NODE_INBOUNDS,
    val inboundsError: String = "",

    val oldUpdateProtocolTemplateReq: UpdateProtocolTemplateReq = UpdateProtocolTemplateReq(),
    val editType: EditType = EditType.CREATE,

    ) {
    val createIsValid: Boolean
        get() = name.isNotEmpty() &&
                inbounds.isNotEmpty() &&
                inboundsError.isEmpty()

    val updateIsValid: Boolean
        get() = inboundsError.isEmpty()

    fun toCreateProtocolTemplateReq(): CreateProtocolTemplateReq {
        return CreateProtocolTemplateReq(
            name = this.name,
            inbounds = this.inbounds
        )
    }

    fun toUpdateProtocolTemplateReq(): UpdateProtocolTemplateReq {
        return UpdateProtocolTemplateReq(
            id = this.id,
            name = this.name,
            inbounds = this.inbounds
        )
    }
}

data class EditProtocolWidget(
    val id: UInt = 0u,
    val name: String = "",
    val nodeId: UInt = 0u,
    val templateId: UInt? = null,
    val status: Status = Status.ENABLE,
    val address: String = "",
    val inbounds: String = NodeConst.NODE_INBOUNDS,
    val port: Int = 80,

    val inboundsError: String = "",
    val expandedBind: Boolean = false,
    val selectedTemplateName: String = "不使用模板",

    val oldUpdateProtocolReq: UpdateProtocolReq = UpdateProtocolReq(),

    val editType: EditType = EditType.CREATE,
) {
    val createIsValid: Boolean
        get() = name.isNotEmpty() &&
                address.isNotEmpty() &&
                inboundsError.isEmpty()
    val updateIsValid: Boolean
        get() = inboundsError.isEmpty()

    fun toCreateProtocolReq(): CreateProtocolReq {
        return CreateProtocolReq(
            name = this.name,
            nodeId = this.nodeId,
            templateId = this.templateId,
            status = this.status.ordinal,
            address = this.address,
            inbounds = this.inbounds,
            port = this.port
        )
    }

    fun toUpdateProtocolReq(): UpdateProtocolReq {
        return UpdateProtocolReq(
            id = this.id,
            name = this.name,
            templateId = this.templateId,
            status = this.status.ordinal,
            address = this.address,
            inbounds = this.inbounds,
            port = this.port
        )
    }
}


data class EditNodeWidget(
    val id: UInt = 0u,
    val name: String = "",
    val nameError: String = "",
    val status: Status = Status.ENABLE,
    val config: String = NodeConst.config,
    val configError: String = "",

    val oldUpdateNodeReq: UpdateNodeReq = UpdateNodeReq(),

    val editType: EditType = EditType.CREATE,

    ) {
    val createIsValid: Boolean
        get() = name.isNotEmpty()
                && nameError.isEmpty()
    val updateIsValid: Boolean
        get() = id > 0u &&
                nameError.isEmpty()

    fun toCreateNodeReq(): CreateNodeReq {
        return CreateNodeReq(
            name = name,
            status = status.ordinal,
            config = config
        )
    }

    fun toUpdateNodeReq(): UpdateNodeReq {
        return UpdateNodeReq(
            id = id,
            name = name,
            status = status.ordinal,
            config = config
        )
    }

}