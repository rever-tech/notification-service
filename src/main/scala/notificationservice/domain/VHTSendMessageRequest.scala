package notificationservice.domain

import com.fasterxml.jackson.annotation.{JsonProperty, JsonValue}

/**
 * @author sonpn
 */
case class VHTSendMessageRequest(submission: SubmissionReq)

case class SubmissionReq(
  @JsonProperty("api_key") apiKey: String,
  @JsonProperty("api_secret") apiSecret: String,
  sms: Seq[VHTMessageInfo]
)

case class VHTMessageInfo(id: String, brandname: String, text: String, to: String)

case class VHTSendMessageResponse(submission: SubmissionResp)

case class SubmissionResp(sms: Seq[VHTMessageInfoResponse])

case class VHTMessageInfoResponse(id: String, status: Int, @JsonProperty("error_message") errorMessage: String = "")