package org.bescala.akkanetworkping

import akka.actor.{Props, ActorLogging, Actor}

object PingResponseCoordinator {
  case class CreatePinger(pingCount: Int, pingInterval: Int)

  def props(): Props = Props(new PingResponseCoordinator)
}

class PingResponseCoordinator extends Actor with ActorLogging {

  val pingServer = context.actorOf(PingServer.props(), "pingServer")
  val pingMaster = context.actorOf(PingMaster.props(pingServer), "pingMaster")

  override def receive: Receive = {
    case createPinger: PingResponseCoordinator.CreatePinger =>
      pingMaster ! createPinger
  }

}
