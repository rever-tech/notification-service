package notificationservice.service

import java.util.UUID

import com.typesafe.config.Config
import notificationservice.domain._
import notificationservice.util.{JsonParser, LoggerUtils, ZConfig}
import notificationservice.util.ZConfig.{ImplicitConfig, _}

import scalaj.http.Http

/**
 * Created by zkidkid on 1/12/17.
 */
abstract class NotificationDelivery {
  def send(notification: Notification): Boolean
}


class SMSNotification(from: String, to: List[String], msg: String) extends Notification("SMS", from, to, msg)

abstract class SMSNotificationDelivery extends NotificationDelivery

case class VHTSMSNotificationDelivery() extends SMSNotificationDelivery {
  val urlSendCode = ZConfig.getString("vht_sms_service.url_send_msg")
  val apiKey = ZConfig.getString("vht_sms_service.api_key")
  val apiSecret = ZConfig.getString("vht_sms_service.api_secret")
  val brandName = ZConfig.getString("vht_sms_service.brand_name")
  val template = ZConfig.getString("vht_sms_service.template", "")

  val smsLogger = LoggerUtils.getLogger("SMSNotificationLog")

  val msgStatusConfig: Config = ZConfig.getConfig("vht_sms_service.message_status")
  val systemStatusConfig: Config = ZConfig.getConfig("vht_sms_service.system_status")

  override def send(notification: Notification): Boolean = {
    // build data from notification & config
    val from: String = notification.from.asInstanceOf[String]
    val to: Seq[String] = notification.to.asInstanceOf[List[String]]
    var message = notification.msg.asInstanceOf[String]
    if (!template.isEmpty) message = template.replaceFirst("\\$message", message)
    // build list message & send data
    var mapPhoneMsgID = Map.empty[String, String]
    val listSms = to.distinct.map(f => {
      val id = genMessageID(f)
      mapPhoneMsgID += (id -> f)
      VHTMessageInfo(id, brandName, message, f)
    })
    val submission = SubmissionReq(apiKey, apiSecret, listSms)
    val sendData = JsonParser.toJson[VHTSendMessageRequest](VHTSendMessageRequest(submission), false)
    // send response
    val resp = Http(urlSendCode).postData(sendData).asString
    val err = systemStatusConfig.getString(resp.code + "", s"Undefined http code:${resp.code}")
    if (err.isEmpty) {
      // parse response & check failed
      val sendMsgResp: VHTSendMessageResponse = JsonParser.fromJson[VHTSendMessageResponse](resp.body)
      val numFailed = getStatusMessage(from, message, mapPhoneMsgID, sendMsgResp.submission.sms)
      if (numFailed == to.size) false else true
    } else {
      smsLogger.error(s"$from\t${to.mkString("[", ",", "]")}\t${message.stripMargin('\n')}\t$err\t$sendData\t${resp.code}\t${resp.body}")
      false
    }
  }

  def getStatusMessage(from: String, message: String, mapMsgID: Map[String, String], sms: Seq[VHTMessageInfoResponse]): Int = {
    sms.map(result => {
      val msg = msgStatusConfig.getString(result.status + "", s"Undefined error status code: ${result.status}, msg: ${result.errorMessage}")
      smsLogger.info(s"$from\t${result.id}\t${mapMsgID.getOrElse(result.id, "")}\t${message.stripMargin('\n')}\t${result.status}\t$msg")
      if (msg.isEmpty) 0 else 1
    }).sum
  }

  def genMessageID(to: String) = UUID.randomUUID().toString
}

class EmailNotification(from: String, to: List[String], msg: AnyRef) extends Notification("EMAIL", from, to, msg)

abstract class EmailNotificationDelivery extends NotificationDelivery

//@ToDo: check https://github.com/softprops/courier for implement
//case class CourierNotification(from:String,to:String,msg:AnyRef) extends EmailNotification(from,to,msg)
//case class CourierEmailNotificationDelivery(notification: Notification)


class SlackNotification(from: String, to: List[String], msg: AnyRef) extends Notification("SLACK", from, to, msg)

case class SlackNotificationDelivery(notification: SlackNotification) extends NotificationDelivery {
  override def send(notification: Notification): Boolean = {
    false
  }
}
