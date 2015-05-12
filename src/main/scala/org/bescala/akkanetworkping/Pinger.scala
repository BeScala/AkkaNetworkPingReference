package org.bescala.akkanetworkping

import akka.actor.{ActorLogging, Actor, Props, ActorRef}
import scala.concurrent.duration.{MILLISECONDS => MS, FiniteDuration, Duration}

object Pinger {
  case class Ping(sequenceNumber: Int)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int, pingTimeout: FiniteDuration): Props =
    Props(new Pinger(pingServer, pingCount, pingInterval, pingTimeout))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int, pingTimeout: FiniteDuration) extends Actor with ActorLogging {

  var curPingCount = pingCount

  // Start pinging @ start
  import context.dispatcher
  for (seq <- 1 to curPingCount) {
    val pingWorker = context.actorOf(PingerWorker.props(pingServer, pingTimeout))
    context.system.scheduler.scheduleOnce(Duration((seq - 1) * pingInterval, MS), pingWorker, Pinger.Ping(seq))
  }

  override def receive: Receive = receiving(pingCount)

  def receiving(outstandingPingSeqNum: Int): Receive = {

    case PingServer.Response(seq) if outstandingPingSeqNum == 1 =>
      context.stop(self)
      log.info("Pinger({}) received final Response({})", self, seq)

    case PingServer.Response(seq) =>
      context.become(receiving(outstandingPingSeqNum - 1))
      log.info("Pinger({}) received   a   Response({})", self, seq)

  }
}
