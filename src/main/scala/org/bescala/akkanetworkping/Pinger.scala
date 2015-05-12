package org.bescala.akkanetworkping

import akka.actor.SupervisorStrategy._
import akka.actor._
import scala.concurrent.duration.{MILLISECONDS => MS, FiniteDuration, Duration}

object Pinger {
  case class Ping(sequenceNumber: Int)
  case class PingTimedout(ping: Ping)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int, pingTimeout: FiniteDuration): Props =
    Props(new Pinger(pingServer, pingCount, pingInterval, pingTimeout))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int, pingTimeout: FiniteDuration) extends Actor with ActorLogging {

  override val supervisorStrategy: SupervisorStrategy = {
    val decider: Decider = {
      case PingerWorker.TimeoutException(ping) =>
        self ! Pinger.PingTimedout(ping)
        Stop
    }
    OneForOneStrategy()(decider = decider orElse super.supervisorStrategy.decider)
  }
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

    case Pinger.PingTimedout(ping) =>
      if ( outstandingPingSeqNum == 1 )
        context.stop(self)
      log.info("Pinger({}), timedout on request {}", self, ping)
      context.become(receiving(outstandingPingSeqNum - 1))
  }
}
