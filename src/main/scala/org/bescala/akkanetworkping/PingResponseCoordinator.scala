package org.bescala.akkanetworkping

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.routing.FromConfig
import scala.concurrent.duration.{MILLISECONDS => MS, FiniteDuration}

object PingResponseCoordinator {
  case class CreatePinger(pingCount: Int, pingInterval: Int)

  def props(): Props = Props(new PingResponseCoordinator)
}

class PingResponseCoordinator extends Actor with ActorLogging {

  val pingServerResponseDelay = FiniteDuration(context.system.settings.config.getDuration("AkkaNetworkPing.PingServer.responseDelay", MS), MS)
  val pingServerReliability = context.system.settings.config.getInt("AkkaNetworkPing.PingServer.reliability")
  private val pingServer: ActorRef = context.actorOf(FromConfig.props(PingServer.props(pingServerResponseDelay, pingServerReliability)), "pingServer")

  val pingMaster = context.actorOf(PingMaster.props(pingServer), "pingMaster")

  override def receive: Receive = {
    case createPinger: PingResponseCoordinator.CreatePinger =>
      pingMaster ! createPinger
  }

}
