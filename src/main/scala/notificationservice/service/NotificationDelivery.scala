package notificationservice.service

/**
 * Created by zkidkid on 1/12/17.
 */
abstract class NotificationDelivery(notification: Notification) {
  def send(notification: Notification): Boolean
}


class SMSNotification(from: String, to: List[String], msg: String) extends Notification("SMS", from, to, msg)

abstract class SMSNotificationDelivery(notification: Notification) extends NotificationDelivery(notification)

case class VHTSMSNotificationDelivery(notification: Notification) extends SMSNotificationDelivery(notification) {
  override def send(notification: Notification): Boolean = {
    false
  }
}

class EmailNotification(from: String, to: List[String], msg: AnyRef) extends Notification("EMAIL", from, to, msg)

abstract class EmailNotificationDelivery(notification: EmailNotification) extends NotificationDelivery(notification)

//@ToDo: check https://github.com/softprops/courier for implement
//case class CourierNotification(from:String,to:String,msg:AnyRef) extends EmailNotification(from,to,msg)
//case class CourierEmailNotificationDelivery(notification: Notification)


class SlackNotification(from: String, to: List[String], msg: AnyRef) extends Notification("SLACK", from, to, msg)

case class SlackNotificationDelivery(notification: SlackNotification) extends NotificationDelivery(notification) {
  override def send(notification: Notification): Boolean = {
    false
  }
}
