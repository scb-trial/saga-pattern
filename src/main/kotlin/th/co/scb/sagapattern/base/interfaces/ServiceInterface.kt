package th.co.scb.sagapattern.base.interfaces

import th.co.scb.sagapattern.base.models.RequestModelBase
import java.util.Optional

interface ServiceBaseInterface {

    fun issue(requestModel: RequestModelBase)
    fun cancel(requestModel: RequestModelBase)
}