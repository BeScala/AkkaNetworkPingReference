package org.bescala.akkanetworkping

import akka.actor.{ActorLogging, Actor, Props, ActorRef}

object Pinger {
  case class Ping(sequenceNumber: Int)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int): Props = Props(new Pinger(pingServer, pingCount, pingInterval))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int) extends Actor with ActorLogging {

  var curPingCount = pingCount

  // Start pinging @ start
  for (seq <- 1 to curPingCount)
    pingServer ! Pinger.Ping(seq)

  override def receive: Receive = {
    case PingServer.Response(seq) if curPingCount == 1 =>
      log.info("Pinger({}) received final Response({})", self, seq)

    case PingServer.Response(seq) =>
      curPingCount -= 1
      log.info("Pinger({}) received   a   Response({})", self, seq)
  }
}
