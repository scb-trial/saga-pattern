package th.co.scb.sagapattern.base.models

import th.co.scb.sagapattern.base.interfaces.ServiceBaseInterface

data class RequestModelBase(
    val refId: String,
    val requestInput: Any,
    val relatedService: Set<ServiceBaseInterface>,
    val processServices: List<ProcessResultModel> = emptyList()
)