package th.co.scb.sagapattern.base.services

import th.co.scb.sagapattern.base.interfaces.ServiceBaseInterface
import th.co.scb.sagapattern.base.models.OrchestrationMode
import th.co.scb.sagapattern.base.models.RequestModelBase
import java.util.UUID
import kotlin.concurrent.thread

abstract class OrchestrationServiceBase {
    abstract val relatedServices: Set<ServiceBaseInterface>
    abstract val mode: OrchestrationMode
    abstract val serviceName: String

    abstract fun validateRequestInput(requestInput: Any)
    fun mainFlow(requestInput: Any) {
        validateRequestInput(requestInput)

        //transform input model
        val requestModel = transformInputModel(requestInput)

        when (mode) {
            OrchestrationMode.SEQUENTIAL -> processSequential(requestModel)
            OrchestrationMode.PARALLEL -> processParallel(requestModel)
        }
    }

    //Method to process for sequential mode
    private fun processSequential(requestModel: RequestModelBase) {
        relatedServices.first().issue(requestModel)
    }

    //Method to process for parallel mode
    private fun processParallel(requestModel: RequestModelBase) {
        relatedServices.forEach { service ->
            thread { service.issue(requestModel) }
        }
    }

    //Method to transform input model
    private fun transformInputModel(requestInput: Any): RequestModelBase {
        return RequestModelBase(
            refId = generateRefId(),
            requestInput = requestInput,
            relatedService = relatedServices,
            processServices = emptyList()
        )
    }

    //Method to generate refId
    fun generateRefId(): String {
        return UUID.randomUUID().toString()
    }

}