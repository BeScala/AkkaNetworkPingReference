 import scala.Console

 shellPrompt in ThisBuild := { state =>
   val exerciseNumber = 8
   val exerciseTitle = "Using the Akka ask pattern - Solution"
   val exerciseTitlePrompt = Console.RED + exerciseTitle + Console.RESET
   val beScala = Console.RESET + "Be" + Console.YELLOW + "Sca" + Console.RED + "la" + Console.RESET
   val exercisePrompt = Console.BLUE + "Exercise " + exerciseNumber + Console.RESET
   s"$beScala - $exercisePrompt - $exerciseTitlePrompt > "
 }
