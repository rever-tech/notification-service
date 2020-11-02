package notificationservice.service

import com.google.inject.Guice
import com.twitter.inject.{Injector, IntegrationTest}
import notificationservice.module.TestSMTPEmailModule
import org.jvnet.mock_javamail.Mailbox

/**
 * Created by phuonglam on 1/12/17.
 **/
class SMTPEmailNotificationServiceTest extends IntegrationTest {
  override protected def injector: Injector = Injector(Guice.createInjector(TestSMTPEmailModule))
  val notificationDelivery = injector.instance[NotificationDelivery]

  "[Email] SMTP MOCK" should {

    val from = "form1@rever.vn"
    val to1 = "to1@rever.vn"
    val to2 = "to2@rever.vn"
    val subject = "Do not reply"
    val body = "Hello, this is a test"

    "send email to 1 user successfully" in {
      Mailbox.clearAll()
      assert(notificationDelivery.send(SMTPEmailNotification(
        from = from,
        to = List(to1),
        msg = (subject, body)
      )))

      val inbox = Mailbox.get(to1)
      assertResult(1)(inbox.size())
      assertResult(subject)(inbox.get(0).getSubject)
      assertResult(body)(inbox.get(0).getContent)
    }

    "send email to 1 user successfully with default subject" in {
      Mailbox.clearAll()
      assert(notificationDelivery.send(SMTPEmailNotification(
        from = from,
        to = List(to1),
        msg = body
      )))

      val inbox = Mailbox.get(to1)
      assertResult(1)(inbox.size())
      assertResult(body)(inbox.get(0).getContent)
    }

    "send email to many user successfully" in {
      Mailbox.clearAll()
      assert(notificationDelivery.send(SMTPEmailNotification(
        from = from,
        to = List(to1, to2),
        msg = (subject, body)
      )))

      val inbox1 = Mailbox.get(to1)
      assertResult(1)(inbox1.size())
      assertResult(subject)(inbox1.get(0).getSubject)
      assertResult(body)(inbox1.get(0).getContent)

      val inbox2 = Mailbox.get(to2)
      assertResult(1)(inbox2.size())
      assertResult(subject)(inbox2.get(0).getSubject)
      assertResult(body)(inbox2.get(0).getContent)
    }

    "send many email to many user successfully" in {
      Mailbox.clearAll()
      assert(notificationDelivery.send(SMTPEmailNotification(
        from = from,
        to = List(to1, to2),
        msg = (subject, body)
      )))
      assert(notificationDelivery.send(SMTPEmailNotification(
        from = from,
        to = List(to1, to2),
        msg = ("this is a subject", "This is a body")
      )))

      val inbox1 = Mailbox.get(to1)
      assertResult(2)(inbox1.size())

      val inbox2 = Mailbox.get(to2)
      assertResult(2)(inbox2.size())
    }

  }
}
