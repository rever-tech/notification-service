package notificationservice.service

import notificationservice.domain.NotificationType

/**
 * Created by zkidkid on 1/12/17.
 */

abstract class NotificationDelivery {
  def send(notification: Notification): Boolean
}

class SMSNotification(override val from: String, override val to: List[String], override val msg: String)
  extends Notification(NotificationType.SMS, from, to, msg)

abstract class SMSNotificationDelivery extends NotificationDelivery

class EmailNotification(override val from: String, override val to: List[String], msg: AnyRef) extends Notification(NotificationType.EMAIL, from, to, msg)

abstract class EmailNotificationDelivery extends NotificationDelivery

class SlackNotification(override val from: String, override val to: List[String], msg: AnyRef) extends Notification(NotificationType.SLACK, from, to, msg)

abstract class SlackNotificationDelivery extends NotificationDelivery