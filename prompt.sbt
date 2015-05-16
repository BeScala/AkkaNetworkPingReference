 import scala.Console

 shellPrompt in ThisBuild := { state =>
   val exerciseNumber = 9
   val exerciseTitle = ""
   val exerciseTitlePrompt = Console.RED + exerciseTitle + Console.RESET
   val beScala = Console.RESET + "Be" + Console.YELLOW + "Sca" + Console.RED + "la" + Console.RESET
   val exercisePrompt = Console.BLUE + "Further suggestions" + Console.RESET
   s"$beScala - $exercisePrompt > "
 }
