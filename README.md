# AkkaNetworkPing

## Initial state
In the initial version, only an Actorsystem (name *Akka-Network-Ping*) is created.

No Actors are created yet, but there's a command line parser that accepts the following commands from the command-line:

	[pingerCount] pi|ping [pingCount] [pingInterval]
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

## Exercise 1

In this exercise, you will define four actors, ```PingResponseCoordinator```, ```PingMaster```, ```Pinger``` and ```PingServer```, each with an empty behaviour and a definition of the messages, ```Ping``` and ```Response```, relevant to the protocol we want to implement.

Your tasks:

1. Change the behaviour of the PingServer actor so that when it receives a ```Ping``` message, it responds to the sender of the message with a ```Response``` message. Note that both messages carry extra information:
  - sequenceNumber: a number that is unique for each ```Ping``` message for a particular ```Ping``` actor.

2. In the ```Response``` response, the value of ```ref``` contained in the ```Ping``` message should be copied as-is.

3. Change the behaviour of the ```Pinger``` actor so that, after creation, it will send ```pingCount``` ```Ping``` messages to the ```PingServer``` actor. The ```sequenceNumber``` should be incremented between subsequent ```Ping``` messages. For the time being, ignore ```pingInterval```: just send the messages in rapid succession.

4. Change the ```PingResponseCoordinator``` actor. When an instance of this actor is created, it should create a ```PingServer``` and a ```PingMaster``` actor as children. Name these actors ```pingServer``` and ```pingMaster``` respectively.

5. When ```PingResponseCoordinator``` receives a ```CreatePinger``` message, it should forward this message to the ```PingMaster``` actor.

6. Create the ```PingResponseCoordinator``` actor in ```PingResponseApp```  (look for the TODO in the code).

7. Change the ```PingMaster``` behaviour as to create a ```Pinger``` child actor when it receives a ```CreatePinger``` message.

8. Adapt the ```createPinger``` method in the ```PingResponseApp``` (look for the TODO in the code) as to send the appropriate messages to the right actor so that the requested number of ```Pinger``` actors are created.

9. Run the application, run some ping commands and verify that the output is what is expected.
 
