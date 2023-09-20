package th.co.scb.sagapattern.base.services

import th.co.scb.sagapattern.base.constants.DUPLICATE_REDIS_KEY
import th.co.scb.sagapattern.base.constants.SERVICE_NAME_IS_NULL
import th.co.scb.sagapattern.base.constants.SERVICE_PROCESS_NOT_FOUND
import th.co.scb.sagapattern.base.exceptions.IllegalFlowException
import th.co.scb.sagapattern.base.interfaces.ServiceBaseInterface
import th.co.scb.sagapattern.base.models.OrchestrationMode
import th.co.scb.sagapattern.base.models.ProcessResultModel
import th.co.scb.sagapattern.base.models.ProcessStatus
import th.co.scb.sagapattern.base.models.RequestModelBase
import th.co.scb.sagapattern.base.utils.BaseHelper.replaceIf
import java.util.UUID
import java.util.function.Predicate
import kotlin.concurrent.thread

abstract class OrchestrationServiceBase {
    abstract val relatedServices: Set<ServiceBaseInterface>
    abstract val mode: OrchestrationMode
    abstract val serviceName: String

    abstract fun validateRequestInput(requestInput: Any)
    fun orchestrateFlow(requestInput: Any) {
        validateRequestInput(requestInput)

        //transform input model
        val requestModel = transformInputModel(requestInput)

        // initial data into redis
        initialDataIntoRedis(requestModel)

        when (mode) {
            OrchestrationMode.SEQUENTIAL -> processSequential(requestModel)
            OrchestrationMode.PARALLEL -> processParallel(requestModel)
        }
    }

    //Method to receive result from service and process
    fun receiveResult(requestModel: RequestModelBase) {
        // fetch data from redis
        //TODO: Maybe change to received in constructor
        val redis = RedisService()
        val redidKey = redis.generateKey(serviceName, requestModel.refId)
        val data = redis.retrieveData(redidKey)

        val serviceName = requestModel.serviceName ?: throw IllegalFlowException(SERVICE_NAME_IS_NULL)

        // process itself
        // check and process rollback
        val updatedData =  validateAndProcessRollback(
            requestModel = requestModel,
            processResult = updateProcessResult(
                redisData = data,
                requestModel = requestModel,
                serviceName = serviceName
            ))
        data.processServices = updatedData.toMutableList()


        // validate result in redis all done
        if (validateAllProcessIsDone(requestModel, updatedData)) {
            redis.deleteData(redidKey)
        } else
            redis.updateData(redidKey, data)

    }

    //Method to  validate all process is done
    fun validateAllProcessIsDone(requestModel: RequestModelBase, processResultModels: List<ProcessResultModel>): Boolean {
        val noOfDoneProcess = processResultModels.count { it.status != ProcessStatus.PROCESSING }
        return requestModel.relatedService.size == noOfDoneProcess
    }

    //Method to update process result in redis data
    private fun updateProcessResult(redisData: RequestModelBase, requestModel: RequestModelBase, serviceName: String): MutableList<ProcessResultModel> {
        val currentProcess = redisData.processServices.find { it.serviceName == serviceName } ?: throw IllegalFlowException(SERVICE_PROCESS_NOT_FOUND)


        val service = getServiceByName(serviceName)
        val resultFlag = if (service.checkResponse(requestModel)) {
            ProcessStatus.SUCCESS
        } else {
            // cancel all process
//            cancelProcess(requestModel, serviceName)
            ProcessStatus.FAILED
        }
        val updatedProcess = currentProcess.copy(status = resultFlag)
        return redisData.processServices.replaceIf(Predicate.isEqual(currentProcess), updatedProcess).toMutableList()

    }

    // Method to validate and process rollback
    private fun validateAndProcessRollback(requestModel: RequestModelBase,
                                           processResult: List<ProcessResultModel>): List<ProcessResultModel> {
        // validate have failed
        if (processResult.find { it.status == ProcessStatus.FAILED } == null) return processResult

        // process rollback for all process that success
        val output = mutableListOf<ProcessResultModel>()
        processResult.forEach { process ->
            if (process.status == ProcessStatus.SUCCESS) {
                cancelProcess(requestModel, process.serviceName)
                output.add(process.copy(status = ProcessStatus.CANCELLED))
            } else {
                output.add(process)
            }
        }
        return output
    }

    //Method to cancel process
    private fun cancelProcess(requestModel: RequestModelBase, serviceName: String) {
        val service = getServiceByName(serviceName)
        val cancelRequest = service.prepareCancelRequestModel(requestModel)
        service.cancel(cancelRequest)
    }



    //Method to get target service from service name
    private fun getServiceByName(serviceName: String): ServiceBaseInterface {
        return relatedServices.first { it.javaClass.simpleName == serviceName }
    }

    //Method to process for target service
    private fun ServiceBaseInterface.processTargetService(requestModel: RequestModelBase) {
        val service = this
        val processResult = ProcessResultModel(
            serviceName = service.serviceName(),
            status = ProcessStatus.PROCESSING
        )
        requestModel.processServices.add(processResult)
        service.issue(service.transformRequestModel(requestModel))
    }

    //Method to process for sequential mode
    private fun processSequential(requestModel: RequestModelBase) {
        val service = relatedServices.first()
        service.processTargetService(requestModel)
    }

    //Method to process for parallel mode
    private fun processParallel(requestModel: RequestModelBase) {
        relatedServices.forEach { service ->
            thread { service.processTargetService(requestModel) }
        }
    }

    //Method to store data into redis
    private fun initialDataIntoRedis(requestModel: RequestModelBase) {
        //TODO: Maybe change to received in constructor
        val redis = RedisService()
        val redidKey = redis.generateKey(serviceName, requestModel.refId)
        // check key in redis
        if (redis.checkKey(redidKey)) throw IllegalFlowException(DUPLICATE_REDIS_KEY)

        redis.storeData(redidKey, requestModel)
    }

    //Method to transform input model
    private fun transformInputModel(requestInput: Any): RequestModelBase {
        return RequestModelBase(
            refId = generateRefId(),
            requestInput = requestInput,
            relatedService = relatedServices,
            processServices = mutableListOf()
        )
    }

    //Method to generate refId
    private fun generateRefId(): String {
        return UUID.randomUUID().toString()
    }

}