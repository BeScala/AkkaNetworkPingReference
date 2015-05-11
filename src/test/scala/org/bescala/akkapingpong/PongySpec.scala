package org.bescala.akkanetworkping

import akka.actor.ActorRef
import akka.testkit.TestProbe
import scala.concurrent.duration._
import scala.language.postfixOps

class PingServerSpec extends BaseAkkaSpec {

  "Sending a Ping to a PingServer actor" should {
    "result in sending a Response response to the sender" in {
      val pinger = TestProbe()
      implicit val _ = pinger.ref // Look at ! method in ActorRef Scaladoc
      val pingServer = createResponseer()
      pinger.within(0 milliseconds, 150 milliseconds) {
        pingServer ! Pinger.Ping(1)
        pinger.expectMsg(PingServer.Response(1))
      }
    }
  }

  def createResponseer(): ActorRef =
    system.actorOf(PingServer.props())
}
