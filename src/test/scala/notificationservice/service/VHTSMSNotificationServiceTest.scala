package notificationservice.service

import com.google.inject.Guice
import com.twitter.inject.{Injector, IntegrationTest}
import notificationservice.module.TestVHTSMSModule

/**
 * @author sonpn
 */
class VHTSMSNotificationServiceTest extends IntegrationTest {
  override protected def injector: Injector = Injector(Guice.createInjector(TestVHTSMSModule))

  "send 1-1" in {

  }

  "send 1-many" in {
    
  }
}
