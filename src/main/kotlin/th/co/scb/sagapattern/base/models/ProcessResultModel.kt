package th.co.scb.sagapattern.base.models

data class ProcessResultModel(
    val serviceName: String,
    val status: ProcessStatus = ProcessStatus.PROCESSING,
    val result: Any? = null,
)
