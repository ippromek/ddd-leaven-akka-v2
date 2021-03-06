package ecommerce.sales.app

import akka.actor.{Actor, ActorLogging, Props}
import akka.http.scaladsl.Http
import akka.util.Timeout
import ecommerce.sales.ReservationOfficeId
import org.json4s.Formats
import pl.newicom.dddd.serialization.JsonSerHints.fromConfig
import pl.newicom.dddd.writefront.HttpCommandHandler

import scala.concurrent.duration.FiniteDuration

object HttpService {
  def props(interface: String, port: Int, askTimeout: FiniteDuration): Props =
    Props(new HttpService(interface, port)(askTimeout))
}

class HttpService(interface: String, port: Int)(implicit val timeout: Timeout)
  extends Actor with SalesFrontConfiguration with HttpCommandHandler with ActorLogging {

  implicit val formats: Formats = fromConfig(config)

  Http(context.system).bindAndHandle(route, interface, port)

  log.info(s"Listening on $interface:$port")

  override def receive = Actor.emptyBehavior

  override def offices = Set(ReservationOfficeId)

  private def route = /*logRequestResult("sales")*/ {
    pathPrefix("ecommerce") {
      path("sales") {
        handle[ecommerce.sales.Command]
      }
    }
  }

}