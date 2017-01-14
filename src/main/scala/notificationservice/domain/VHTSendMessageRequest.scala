package notificationservice.domain

/**
 * @author sonpn
 */
case class VHTSendMessageRequest(submission: SubmissionReq)

case class SubmissionReq(apiKey: String, apiSecret: String, sms: Seq[VHTMessageInfo])

case class VHTMessageInfo(id: String, brandname: String, text: String, to: String)

case class VHTSendMessageResponse(submission: SubmissionResp)

case class SubmissionResp(sms: Seq[VHTMessageInfoResponse])

case class VHTMessageInfoResponse(id: String, status: Int)