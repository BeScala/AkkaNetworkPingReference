package org.bescala.akkanetworkping

import akka.actor._

import scala.concurrent.duration.FiniteDuration

object PingServer {
  case class Response(sequenceNumber: Int)

  def props(responseDelay: FiniteDuration, pingServerReliability: Int): Props = Props(new PingServer(responseDelay, pingServerReliability))

}

class PingServer(responseDelay: FiniteDuration, pingServerReliability: Int) extends Actor with ActorLogging with Stash {

  override def receive: Receive = waitingForAPing

  val randomNumber = scala.util.Random

  def waitingForAPing: Receive = {
    case ping @ Pinger.Ping(_) if randomNumber.nextInt(100) > pingServerReliability =>
      // Just ignore message...
      log.info("Dropped ping request {}", ping)
    case Pinger.Ping(seq) =>
      import context.dispatcher
      context.system.scheduler.scheduleOnce(responseDelay, self, PingServer.Response(seq))
      context.become(replyingToAPing(sender()))
  }

  def replyingToAPing(sndr: ActorRef): Receive = {
    case pong @ PingServer.Response(seq) =>
      unstashAll()
      sndr ! pong
      context.become(waitingForAPing)
    case _ =>
      stash()
  }

}
