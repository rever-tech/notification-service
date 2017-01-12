package notificationservice.service

/**
 * Created by zkidkid on 1/12/17.
 */
class SlackNotification(from: String, to: List[String], msg: AnyRef) extends Notification("SLACK", from, to, msg)
case class SlackNotificationDelivery() extends NotificationDelivery {

  override def send(notification: Notification): Boolean = {
    false
  }
}
