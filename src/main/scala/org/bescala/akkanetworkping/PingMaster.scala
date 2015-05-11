package org.bescala.akkanetworkping

import akka.actor.{Props, ActorRef, ActorLogging, Actor}

object PingMaster {

  def props(pingServer: ActorRef): Props = Props(new PingMaster(pingServer))
}

class PingMaster(pingServer: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive =
    // TODO: Implement behaviour
    Actor.emptyBehavior
}
