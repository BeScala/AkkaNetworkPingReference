package org.bescala.akkanetworkping

import akka.actor.{ActorLogging, Actor, Props, ActorRef}
import scala.concurrent.duration.Duration
import scala.concurrent.duration.{ MILLISECONDS => MS }

object Pinger {
  case class Ping(sequenceNumber: Int)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int): Props = Props(new Pinger(pingServer, pingCount, pingInterval))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int) extends Actor with ActorLogging {

  var curPingCount = pingCount

  // Start pinging @ start
  import context.dispatcher
  for (seq <- 1 to curPingCount)
    context.system.scheduler.scheduleOnce(Duration((seq - 1) * pingInterval, MS), pingServer, Pinger.Ping(seq))

  override def receive: Receive = {

    case PingServer.Response(seq) if curPingCount == 1 =>
      context.stop(self)
      log.info("Pinger({}) received final Response({})", self, seq)

    case PingServer.Response(seq) =>
      curPingCount -= 1
      log.info("Pinger({}) received   a   Response({})", self, seq)
  }
}
