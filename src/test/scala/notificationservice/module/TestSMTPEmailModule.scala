package notificationservice.module

import javax.inject.Singleton

import com.google.inject.Provides
import com.twitter.inject.TwitterModule
import notificationservice.service.{SMTPEmailNotificationDelivery, NotificationDelivery}

/**
 * Created by phuonglam on 1/12/17.
 **/
object TestSMTPEmailModule extends TwitterModule {

  @Singleton
  @Provides
  def providesNotificationDelivery: NotificationDelivery = SMTPEmailNotificationDelivery()
}
