package org.bescala.akkanetworkping

import akka.actor.ActorSystem
import akka.event.Logging

import scala.annotation.tailrec
import scala.io.StdIn

object NetworkPingApp {

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("Akka-Network-Ping")

    val pingResponseApp = new NetworkPingApp(system)
    pingResponseApp.run()
  }
}

class NetworkPingApp(system: ActorSystem) extends CommandReader {

  private val log = Logging(system, getClass.getName)

  def run(): Unit = {
    log.warning(f"{} running%nEnter commands into the terminal, e.g. `q` or `quit`", getClass.getSimpleName)
    commandLoop()
    system.awaitTermination()
  }

  @tailrec
  private def commandLoop(): Unit =
    Command(StdIn.readLine()) match {
      case Command.Pinger(pingerCount, pingCount, pingInterval) =>
        createPinger(pingerCount, pingCount, pingInterval)
        commandLoop()
      case Command.Status =>
        status()
        commandLoop()
      case Command.Quit =>
        system.shutdown()
      case Command.Unknown(command) =>
        log.warning("Unknown command {}!", command)
        commandLoop()
    }

  // TODO: Create networkPingCoordinator actor here
  val networkPingCoordinator = system.actorOf(PingResponseCoordinator.props(), "networkPingCoordinator")


  protected def createPinger(pingerCount: Int, pingCount: Int, pingInterval: Int) =
    // TODO: Add appropriate action to trigger the creation of Pinger actor(s)
    for ( _ <- 1 to pingerCount)
      networkPingCoordinator ! PingResponseCoordinator.CreatePinger(pingCount, pingInterval)

  protected def status(): Unit =
    // TODO: Implement status -  it should log a message @ info showing the number of Pinger actors currently running
    log.info("Status command")
}
