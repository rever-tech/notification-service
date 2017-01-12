package notificationservice.service

import com.google.inject.Guice
import com.twitter.inject.{IntegrationTest, Injector}
import notificationservice.module.TestSMTPEmailModule

/**
 * Created by phuonglam on 1/12/17.
 **/
class SMTPRegularEmailNotificationServiceTest extends IntegrationTest {
  override protected def injector: Injector = Injector(Guice.createInjector(TestSMTPEmailModule))
  val notificationDelivery = injector.instance[NotificationDelivery]

  "[Email] SMTP regular" should {
    "send successfully" in {
      assert(notificationDelivery.send(SMTPEmailNotification(
        from = "phuong@rever.vn",
        to = List("phuong@rever.vn", "duy@rever.vn", "quang@rever.vn"),
        msg = ("Hello", "this is a test email")
      )))
    }

  }


}
