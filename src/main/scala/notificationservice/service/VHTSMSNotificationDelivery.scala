package notificationservice.service

import java.util.UUID

import com.fasterxml.jackson.annotation.JsonProperty
import com.typesafe.config.Config
import notificationservice.util.ZConfig.ImplicitConfig
import notificationservice.util.{JsonParser, LoggerUtils, ZConfig}

import scalaj.http.Http

/**
 * @author sonpn
 */

case class VHTSendMessageRequest(submission: SubmissionReq)

case class SubmissionReq(@JsonProperty("api_key") apiKey: String, @JsonProperty("api_secret") apiSecret: String, sms: Seq[VHTMessageInfo])

case class VHTMessageInfo(id: String, brandname: String, text: String, to: String)

case class VHTSendMessageResponse(submission: SubmissionResp)

case class SubmissionResp(sms: Seq[VHTMessageInfoResponse])

case class VHTMessageInfoResponse(id: String, status: Int, @JsonProperty("error_message") errorMessage: String = "")

case class VHTSMSNotificationDelivery() extends SMSNotificationDelivery {
  val urlSend = ZConfig.getString("vht_sms_service.url_send_msg")
  val apiKey = ZConfig.getString("vht_sms_service.api_key")
  val apiSecret = ZConfig.getString("vht_sms_service.api_secret")
  val brandName = ZConfig.getString("vht_sms_service.brand_name")
  val template = ZConfig.getString("vht_sms_service.template", "")

  val smsLogger = LoggerUtils.getLogger("SMSNotificationLog")

  val vhtMsgStatus: Config = ZConfig.getConfig("vht_sms_service.message_status")
  val vhtSystemStatus: Config = ZConfig.getConfig("vht_sms_service.system_status")

  override def send(notification: Notification): Boolean = {
    val smsNotification = notification.asInstanceOf[SMSNotification]
    val message = if (!template.isEmpty) template.replaceFirst("\\$message", smsNotification.msg) else smsNotification.msg
    // build list message & send data
    var mapPhoneMsgID = Map.empty[String, String]
    val listSms = smsNotification.to.distinct.map(f => {
      val id = genMessageID(f)
      mapPhoneMsgID += (id -> f)
      VHTMessageInfo(id, brandName, message, f)
    })
    val submission = SubmissionReq(apiKey, apiSecret, listSms)
    val sendData = JsonParser.toJson[VHTSendMessageRequest](VHTSendMessageRequest(submission), pretty = false)
    // send response
    val resp = Http(urlSend).postData(sendData).asString
    val err = vhtSystemStatus.getString(resp.code + "", s"Undefined http code:${resp.code}")
    if (err.isEmpty) {
      // parse response & check failed
      val sendMsgResp: VHTSendMessageResponse = JsonParser.fromJson[VHTSendMessageResponse](resp.body)
      val numFailed = getStatusMessage(smsNotification.from, message, mapPhoneMsgID, sendMsgResp.submission.sms)
      if (numFailed == smsNotification.to.size) false else true
    } else {
      smsLogger.error(s"${smsNotification.from}\t${smsNotification.to.mkString("[", ",", "]")}\t${message.stripMargin('\n')}\t$err\t$sendData\t${resp.code}\t${resp.body}")
      false
    }
  }

  def getStatusMessage(from: String, message: String, mapMsgID: Map[String, String], sms: Seq[VHTMessageInfoResponse]): Int = {
    sms.map(result => {
      val msg = vhtMsgStatus.getString(result.status + "", s"Undefined error status code: ${result.status}, msg: ${result.errorMessage}")
      smsLogger.info(s"$from\t${result.id}\t${mapMsgID.getOrElse(result.id, "")}\t${message.stripMargin('\n')}\t${result.status}\t$msg")
      if (msg.isEmpty) 0 else 1
    }).sum
  }

  def genMessageID(to: String) = UUID.randomUUID().toString
}
