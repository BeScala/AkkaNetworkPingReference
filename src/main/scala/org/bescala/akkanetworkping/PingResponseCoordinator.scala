package org.bescala.akkanetworkping

import akka.actor.{Props, ActorLogging, Actor}

object PingResponseCoordinator {
  case class CreatePinger(pingCount: Int, pingInterval: Int)

  def props(): Props = Props(new PingResponseCoordinator)
}

class PingResponseCoordinator extends Actor with ActorLogging {

  override def receive: Receive =
    // TODO: Implement behaviour
    Actor.emptyBehavior

}
