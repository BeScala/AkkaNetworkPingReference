package org.bescala.akkanetworkping

import akka.actor._

import scala.concurrent.duration.FiniteDuration

object PingerWorker {
  case class Timedout(ping: Pinger.Ping)
  case class TimeoutException(ping: Pinger.Ping) extends IllegalStateException("Ping timeout")

  def props(pingServer: ActorRef, pingTimeout: FiniteDuration): Props = Props(new PingerWorker(pingServer, pingTimeout))
}
class PingerWorker(pingServer: ActorRef, pingTimeout: FiniteDuration) extends Actor with ActorLogging {

  var timer: Cancellable = _
  override def receive: Receive = {

    case ping: Pinger.Ping =>
      pingServer ! ping
      import context.dispatcher
      timer = context.system.scheduler.scheduleOnce(pingTimeout, self, PingerWorker.Timedout(ping))

    case PingerWorker.Timedout(ping) =>
      log.info("Ping timeout for message {}", ping)
      throw PingerWorker.TimeoutException(ping)

    case pong: PingServer.Response =>
      context.parent ! pong
      timer.cancel()
      context.stop(self)
  }

}
