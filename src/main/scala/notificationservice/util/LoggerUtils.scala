package notificationservice.util

import org.slf4j.{Logger, LoggerFactory}

/**
 * @author sonpn
 */
object LoggerUtils {
  def getLogger(name: String): Logger = {
    LoggerFactory.getLogger(name)
  }
}
