package org.bescala.akkanetworkping

import akka.actor._

import scala.concurrent.duration.FiniteDuration

object PingServer {
  case class Response(sequenceNumber: Int)

  def props(responseDelay: FiniteDuration): Props = Props(new PingServer(responseDelay))

}

class PingServer(responseDelay: FiniteDuration) extends Actor with ActorLogging with Stash {

  override def receive: Receive = waitingForAPing

  def waitingForAPing: Receive = {
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
