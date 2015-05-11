package org.bescala.akkanetworkping

import akka.actor.{ActorLogging, Actor, Props, ActorRef}

object Pinger {
  case class Ping(sequenceNumber: Int)

  def props(pingServer: ActorRef, pingCount: Int, pingInterval: Int): Props = Props(new Pinger(pingServer, pingCount, pingInterval))

}

class Pinger(pingServer: ActorRef, pingCount: Int, pingInterval: Int) extends Actor with ActorLogging {

  override def receive: Receive =
  // TODO: Implement behaviour
    Actor.emptyBehavior
}
