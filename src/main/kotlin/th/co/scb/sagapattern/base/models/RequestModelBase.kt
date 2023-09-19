package th.co.scb.sagapattern.base.models

import th.co.scb.sagapattern.base.interfaces.ServiceBaseInterface

data class RequestModelBase(
    val refId: String,
    val requestInput: Any,
    val responseData: Any? = null,
    val serviceName: String? = null,
    val relatedService: Set<ServiceBaseInterface>,
    var processServices: MutableList<ProcessResultModel> = mutableListOf()
)