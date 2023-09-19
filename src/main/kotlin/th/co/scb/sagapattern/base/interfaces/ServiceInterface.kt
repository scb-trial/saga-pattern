package th.co.scb.sagapattern.base.interfaces

import th.co.scb.sagapattern.base.models.RequestModelBase
import java.util.Optional

interface ServiceBaseInterface {

    fun serviceName(): String
    fun issue(requestModel: RequestModelBase)
    fun cancel(requestModel: RequestModelBase)
    fun transformRequestModel(requestModel: RequestModelBase): RequestModelBase
    fun prepareCancelRequestModel(requestModel: RequestModelBase): RequestModelBase
    fun checkResponse(requestModel: RequestModelBase): Boolean
}