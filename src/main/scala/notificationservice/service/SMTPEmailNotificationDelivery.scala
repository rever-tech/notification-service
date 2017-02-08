package notificationservice.service

import java.util.Properties
import javax.mail.internet.{InternetAddress, MimeMessage}
import javax.mail.{Session, _}

import com.twitter.inject.Logging
import com.twitter.util.NonFatal
import notificationservice.util.ZConfig

import scala.language.implicitConversions

/**
 * Created by phuonglam on 1/12/17.
 **/

class SMTPEmailNotification(override val from: String, override val to: List[String], msg: AnyRef) extends EmailNotification(from, to, msg)

object SMTPEmailNotification {
  def apply(from: String, to: List[String], msg: AnyRef) = new SMTPEmailNotification(from, to, msg)
}

case class SMTPEmailNotificationDelivery(configKey: String = "email") extends NotificationDelivery() with Logging {

  private[this] val session = {
    val props = new Properties()
    props.put("mail.smtp.host", ZConfig.getString(s"$configKey.host"))
    props.put("mail.smtp.port", ZConfig.getString(s"$configKey.port"))
    props.put("mail.smtp.auth", ZConfig.getString(s"$configKey.auth"))
    props.put("mail.smtp.starttls.enable", ZConfig.getString(s"$configKey.tls"))
    Session.getInstance(props, new Authenticator {
      override protected def getPasswordAuthentication: PasswordAuthentication = {
        new PasswordAuthentication(ZConfig.getString(s"$configKey.username"), ZConfig.getString(s"$configKey.password")
        )
      }
    })
  }

  val subject = ZConfig.getString(s"$configKey.default_subject", "")

  override def send(notification: Notification): Boolean = try {
    val courier = notification.asInstanceOf[SMTPEmailNotification]
    val msg = new MimeMessage(session) {
      setFrom(courier.from)
      courier.to.foreach(addRecipient(Message.RecipientType.TO, _))
      courier.msg match {
        case s: String =>
          setSubject(subject)
          setText(s)
        case (sub: String, body: String) =>
          setSubject(sub)
          setText(body)
        case _ => ;
      }
    }
    Transport.send(msg)
    true
  } catch {
    case NonFatal(throwable) =>
      logger.debug("Send email exception", throwable)
      false
  }

  implicit def S2IA(s: String): InternetAddress = new InternetAddress(s)
}

