package notificationservice.module

import com.google.inject.name.Named
import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule
import notificationservice.domain.NotificationType
import notificationservice.service.{KafkaDeliveryConsumerImpl, NotificationDelivery, SMTPEmailNotificationDelivery}
import notificationservice.util.ZConfig

/**
 * Created by zkidkid on 1/12/17.
 */
object NotificationServiceModule extends TwitterModule {

  override def singletonStartup(injector: com.twitter.inject.Injector): Unit = {
    super.singletonStartup(injector)

    if (ZConfig.getBoolean("kafka.delivery.enable", default = false)) {
      val topics = ZConfig.getStringList("kafka.delivery.topics")
      val minBashSize = ZConfig.getInt("kafka.delivery.min_bash_size", 1)
      val properties = ZConfig.getMap("kafka.delivery.properties")

      val deliveryServices = Map(
        NotificationType.EMAIL -> injector.instance[NotificationDelivery]("email"),
//        NotificationType.SLACK -> injector.instance[NotificationDelivery]("slack"),
        NotificationType.SMS -> injector.instance[NotificationDelivery]("sms")
      )
      val kakkaDeliveryConsumer = new KafkaDeliveryConsumerImpl(topics, minBashSize, properties, deliveryServices)
      kakkaDeliveryConsumer.start()
    }
  }

  @Singleton
  @Provides
  @Named("email")
  def providesEmailNotificationDelivery(): NotificationDelivery = {
    SMTPEmailNotificationDelivery("email")
  }
}
