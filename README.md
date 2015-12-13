# AkkaNetworkPing

## Initial state
In the initial version, only an Actorsystem (name *Akka-Network-Ping*) is created.

No Actors are created yet, but there's a command line parser that accepts the following commands from the command-line:

	[pingerCount] pi|ping [pingInterval] [pingCount]
	s|status
	q|quit

The *ping* (alias *pi*) will, in a subsequent exercise, create ```pingerCount``` ```Pinger``` actor(s) that will ping a single ```PingServer``` actor. The PingServer actor should respond to a Ping message with a Response message to the actor who sent the Ping message.

In this state of the application, messages are logged at ```INFO``` level.

Note that the logging output is directed to a log file named ```pingpong.log``` in the project's root folder.

The overall Actor hierarchy will looks as follows:


	                            /user
	                              |
	                     /PingResponseCoordinator
	                              |
	            +-----------------+----------------+
	            |                                  |
	       /pingMaster                           /pingServer
	            |
	  +---------+---------+---------+
	  |         |         |         |
	/pinger1   /pinger2    ...      /pingerN
	

In the first exercise, you will set-up actors *PingResponseCoordinator*, *PingMaster*, *Pinger* and *PingServer*.
