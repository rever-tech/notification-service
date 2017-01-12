package notificationservice.service

import com.twitter.inject.Logging


/**
 * Created by zkidkid on 1/12/17.
 */



case class Notification(notificationType:String,from:AnyRef,to:AnyRef,msg:AnyRef)

trait NotificationService {
  def send(notification: Notification) :Boolean
}

case class LoggableNotificationService(delivery:Map[String,NotificationDelivery]) extends NotificationService with Logging {
  override def send(notification: Notification): Boolean = {
    debug("process " + notification)
    delivery.get(notification.notificationType) match {
      case None => {
        error("No Delivery Found For " + notification.notificationType)
        false
      }
      case Some(delivery) => {
        delivery.send(notification)
      }
    }
  }
}