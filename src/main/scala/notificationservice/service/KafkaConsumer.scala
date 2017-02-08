package notificationservice.service

import javax.inject.Inject

import com.twitter.inject.Logging
import notificationservice.util.JsonParser
import org.apache.kafka.clients.consumer.KafkaConsumer

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
 * @author sonpn
 */
abstract class KafkaDeliveryConsumer[T](topic: Seq[String], minBashSize: Int, properties: Map[String, Object]) extends Thread {
  val consumer = new KafkaConsumer[String, T](properties)
  consumer.subscribe(topic)

  def exec(datas: Seq[T]): Unit

  override def run(): Unit = {
    val buffer = mutable.Buffer[T]()
    while (true) {
      val records = consumer.poll(1000)
      val it = records.iterator()
      while (it.hasNext) {
        buffer += it.next().value()
      }
      if (buffer.length >= minBashSize) {
        try {
          exec(buffer)
          consumer.commitSync()
        } catch {
          case e: Exception =>
          case _ =>
        }
        buffer.clear()
      }
    }
  }
}

class KafkaDeliveryConsumerImpl @Inject()(
  topic: Seq[String],
  minBashSize: Int,
  properties: Map[String, Object],
  deliveryServices: Map[String, NotificationDelivery]
) extends KafkaDeliveryConsumer[String](topic, minBashSize, properties) with Logging {
  override def exec(datas: Seq[String]): Unit = {
    datas.foreach(data => {
      val notification = JsonParser.fromJson[Notification](data)
      deliveryServices.get(notification.notificationType) match {
        case Some(service) => if (service.send(notification)) error(s"1\t0\t$data")
        case _ => error(s"0\t0\t$data")
      }
    })
  }
}