package org.bescala.akkanetworkping

import akka.actor.ActorRef
import akka.testkit.TestProbe
import scala.concurrent.duration._
import scala.language.postfixOps

class PingerSpec extends BaseAkkaSpec {

  "Creating a Pinger actor" should {
    "result in sending the specified number of Ping messages to a Response actor" in {
      val nPings = 1
      val pingIntervalMS = 50
      val pingServer = TestProbe()
      pingServer.within(0 milliseconds, (nPings + 1) * pingIntervalMS milliseconds) {
        val pingy = createPinger(pingServer.ref, nPings, pingIntervalMS)
        for (n <- 1 to nPings) pingServer.expectMsg(Pinger.Ping(n))
      }
    }
  }

  def createPinger(pingServer: ActorRef, nPings: Int, pingIntervalMS: Int): ActorRef =
    system.actorOf(Pinger.props(pingServer, nPings, pingIntervalMS))
}
