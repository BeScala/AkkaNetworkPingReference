package org.bescala.akkanetworkping

import akka.testkit.TestProbe

class PingResponseCoordinatorSpec extends BaseAkkaSpec {
  "Creating PingResponseCoordinator" should {
    "result in creating a child actor with the name 'pingServer' and a child actor with the name 'pingMaster'" in {
      system.actorOf(PingResponseCoordinator.props(), "create-ppCoordinator")
      TestProbe().expectActor("/user/create-ppCoordinator/pingMaster")
      TestProbe().expectActor("/user/create-ppCoordinator/pingServer")
    }
  }
}
