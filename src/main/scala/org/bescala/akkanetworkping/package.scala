package org.bescala

package object akkanetworkping {
  import scala.util.parsing.combinator.RegexParsers

  trait CommandReader {

    protected sealed trait Command

    protected object Command {

      case class Pinger(pingerCount: Int, pingCount: Int, pingInterval: Int) extends Command

      case object Status extends Command

      case object Quit extends Command

      case class Unknown(command: String) extends Command

      def apply(command: String): Command =
        CommandParser.parseAsCommand(command)
    }

    private object CommandParser extends RegexParsers {

      def parseAsCommand(s: String): Command =
        parseAll(parser, s) match {
          case Success(command, _) => command
          case _                   => Command.Unknown(s)
        }

      def Pinger: Parser[Command.Pinger] =
        opt(int) ~ ("pinger|pi".r ~> opt(int) ~ opt(int)) ^^ {
          case pingerCount ~ ( pingCount ~ pingInterval) =>
            Command.Pinger(
              pingerCount getOrElse 1,
              pingCount getOrElse 1,
              pingInterval getOrElse 1000
            )
        }

      def getStatus: Parser[Command.Status.type] =
        "status|s".r ^^ (_ => Command.Status)

      def quit: Parser[Command.Quit.type] =
        "quit|q".r ^^ (_ => Command.Quit)

      def int: Parser[Int] =
        """\d+""".r ^^ (_.toInt)
    }

    private val parser: CommandParser.Parser[Command] =
      CommandParser.Pinger | CommandParser.getStatus | CommandParser.quit
  }
}
