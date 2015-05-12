package org.bescala.akkanetworkping

import akka.actor.{Props, ActorRef, ActorLogging, Actor}

import scala.concurrent.duration.{ MILLISECONDS => MS, Duration}

object PingMaster {

  def props(pingServer: ActorRef): Props = Props(new PingMaster(pingServer))
}

class PingMaster(pingServer: ActorRef) extends Actor with ActorLogging {

  val pingTimeout = Duration(context.system.settings.config.getDuration("AkkaNetworkPing.Ping.pingTimeout", MS), MS)

  override def receive: Receive = {
    case PingResponseCoordinator.CreatePinger(pingCount, pingInterval) =>
      context.actorOf(Pinger.props(pingServer, pingCount, pingInterval, pingTimeout))
  }
}
