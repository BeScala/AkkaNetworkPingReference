 import scala.Console

 shellPrompt in ThisBuild := { state =>
   val exerciseNumber = 3
   val exerciseTitle = "Introduce reaction time in Pongy actor - (Alt) Solution"
   val exerciseTitlePrompt = Console.RED + exerciseTitle + Console.RESET
   val beScala = Console.RESET + "Be" + Console.YELLOW + "Sca" + Console.RED + "la" + Console.RESET
   val exercisePrompt = Console.BLUE + "Exercise " + exerciseNumber + Console.RESET
   s"$beScala - $exercisePrompt - $exerciseTitlePrompt > "
 }
