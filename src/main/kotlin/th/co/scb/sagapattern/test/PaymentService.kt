package th.co.scb.sagapattern.test

import th.co.scb.sagapattern.base.interfaces.ServiceBaseInterface
import th.co.scb.sagapattern.base.models.RequestModelBase
import java.util.*

class PaymentService: ServiceBaseInterface {
    override fun serviceName(): String {
        return "PAYMENT"
    }

    override fun issue(requestModel: RequestModelBase) {
        TODO("Not yet implemented")
    }

    override fun cancel(requestModel: RequestModelBase) {
        TODO("Not yet implemented")
    }

    override fun transformRequestModel(requestModel: RequestModelBase): RequestModelBase {
        return requestModel
    }

    override fun prepareCancelRequestModel(requestModel: RequestModelBase): RequestModelBase {
        return requestModel
    }
}
