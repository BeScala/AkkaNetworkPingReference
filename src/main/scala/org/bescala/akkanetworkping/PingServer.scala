package org.bescala.akkanetworkping

import akka.actor.{Actor, ActorLogging, Props, ActorRef}

object PingServer {
  case class Response(sequenceNumber: Int)

  def props(): Props = Props(new PingServer)

}

class PingServer extends Actor with ActorLogging {

  override def receive: Receive =
  // TODO: Implement behaviour
    Actor.emptyBehavior

}
