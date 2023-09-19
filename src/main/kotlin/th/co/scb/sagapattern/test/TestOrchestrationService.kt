package th.co.scb.sagapattern.test

import th.co.scb.sagapattern.base.models.OrchestrationMode
import th.co.scb.sagapattern.base.services.OrchestrationServiceBase

class TestOrchestrationService: OrchestrationServiceBase() {
    override val relatedServices = setOf(PaymentService(), OrderService())
    override val mode = OrchestrationMode.SEQUENTIAL
    override val serviceName = TEST_ORCHESTRATION_SERVICE_NAME

}