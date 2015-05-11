 import scala.Console

 shellPrompt in ThisBuild := { state =>
   val exerciseNumber = 2
   val exerciseTitle = "Sending pings at regular intervals - Solution"
   val exerciseTitlePrompt = Console.RED + exerciseTitle + Console.RESET
   val beScala = Console.RESET + "Be" + Console.YELLOW + "Sca" + Console.RED + "la" + Console.RESET
   val exercisePrompt = Console.BLUE + "Exercise " + exerciseNumber + Console.RESET
   s"$beScala - $exercisePrompt - $exerciseTitlePrompt > "
 }
