package notificationservice.service

import com.google.inject.Guice
import com.twitter.inject.{Injector, IntegrationTest}
import notificationservice.module.TestVHTSMSModule

/**
 * @author sonpn
 */
class VHTSMSNotificationServiceTest extends IntegrationTest {
  override protected def injector: Injector = Injector(Guice.createInjector(TestVHTSMSModule))

  val vHTSMSNotificationDelivery = injector.instance[NotificationDelivery]

  "test" in {}
  
  "send failed" in {
    val sendObj = new SMSNotification("0123456789", List("123456789"), "test message")
    assertResult(false)(vHTSMSNotificationDelivery.send(sendObj))
  }

  "send 1-1" in {
    val sendObj = new SMSNotification("0123456789", List("01266773414"), "test message 1-1")
    assertResult(true)(vHTSMSNotificationDelivery.send(sendObj))
  }

  "send 1-many" in {
    val sendObj = new SMSNotification("0123456789", List("01266773414", "0989317032"), "test message 1-many")
    assertResult(true)(vHTSMSNotificationDelivery.send(sendObj))
  }
}
