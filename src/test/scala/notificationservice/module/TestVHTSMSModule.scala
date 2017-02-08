package notificationservice.module

import com.twitter.inject.TwitterModule
import com.google.inject.{Inject, Provides, Singleton}
import notificationservice.service.{NotificationDelivery, VHTSMSNotificationDelivery}

/**
 * @author sonpn
 */
object TestVHTSMSModule extends TwitterModule {
  @Singleton
  @Provides
  def providesSMSNotificationService(): NotificationDelivery = VHTSMSNotificationDelivery()
}
