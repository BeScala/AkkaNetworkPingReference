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

## Exercise 1 - Solution and discussion

This code implements a solution for the stated tasks. However, some questions may be raised:

- How to implement the ```pingInterval``` behaviour - when ```pingCount``` > 1: the first ```Ping``` should be sent immediately and subsequent ```Ping``` messages should be sent sequentially at ```pingInterval``` intervals.

- In the current application, there's an obvious memory leak: ```Ping``` actors are created and, after having performed their task, are never stopped...

- A number of tests have been added to the project that test the functionality of the ```Pinger``` and ```PingServer``` actors.

##Exercise 2 - Implementing pingInterval

1. Change ```Pinger``` to send the ```Ping``` messages at the specified interval. Have a look at an actor's access to the scheduler (```context.system.scheduler.*```). Note that we always send ```pingCount``` messages, i.e. regardless of the number of ```Response``` messages that are sent back in response.

2. Fix the memory leak, for example, let the ```Ping``` actor stop itself when it has done its job. For this, look at what's available under the actor's context.

##Exercise 2 - Solution & discussion

The sending of ```Ping``` messages at regular intervals can be implemented in many ways... In the given solution, the ```Pinger``` actor uses the scheduler to schedule delivery of the required ```Ping``` messages to itself at the right moment.

- Also note the utilisation of ```context.stop(self)``` to stop the ```Pinger``` actor.

- Notice the need to bring an (implicit) execution scope into context - this is needed by the ```scheduleOnce``` method

##Exercise 3 - Introduce reaction time in PingServer actor

In this exercise, we will let ```PingServer``` actor respond to ```Ping``` messages with a delay configurable in the application config settings. Change the signature of the ```PingServer``` actor as to accept this delay parameter of type ```FiniteDuration```.

Name the configuration parameter ```AkkaNetworkPing.Response.responseDelay```

Perform some ping tests with the ```responseDelay``` set to 2 seconds. What is the impact on the behaviour of the application? What is causing this effect?

##Exercise 3 - Solution & discussion

In the proposed solution, we make sure that the ```PingServer``` actor is really busy while responding to a ```Ping```. We do this by ignoring any incoming message during during the processing of the ```Ping```. In order to achieve this, we utilise the Akka ```Stash``` trait.

##Exercise 3 - Solution - Alternative ```Pinger``` implementation

An alternative for the utilisation of mutable state in the ```Pinger``` actor.

##Exercise 4 - Scale the ```PingServer``` actor

As observed during some testing, the single instance of the ```PingServer``` actor has become a limiting factor for the ping-pong throughput.

Use a pooled router with round-robin routing strategy configured via Akka configuration to scale the app.

##Exercise 4 - Solution & discussion

Making the *Akka-Network-Ping* scale can be achieved by removing the bottleneck in the ```PingServer``` actor by turning it into a pooled router. Notice the elegance by which this can be done: a simply change in the deployment configuration and a change on one line in the source code.

##Exercise 5 - Introduction of an unreliable ```PingServer``` actor

Let's make ```PingServer``` a bit unreliable by having it not send a reply to a ```Ping``` message at random: 

- Define a configuration parameter named ```AkkaNetworkPing.Response.reliability``` with a value between 0 and 100.
- Change the signature of ```PingServer``` as to accept the ```reliability``` parameter of type ```Int```.
- Change the behaviour of ```PingServer``` so that when it receives a ```Ping```, it throws a dice by generating a random number between 0 and 100. If this number is greater than ```reliability```, respond. Otherwise, ignore the message and continue waiting for a new ```Ping```.

##Exercise 5 - Solution & discussion

After the modification, we see that, in case of dropped ```Ping``` messages, the bookkeeping of the ```Pinger``` no longer fits. As a consequence, it never stop because it will keep waiting for responses that will never arrive.

Observe this by running the application a number of times, setting ```reliability``` to for example 60 until at least one message is dropped.

##Exercise 6 - Delegating work to one-off actors

In this exercise you will change the behaviour of the ```Pinger``` actor in a number of ways:

- Instead of ```Pinger``` sending a ```Ping``` message itself, it will delegate this task to a one-off actor named ```PingerWorker```

- ```PingerWorker``` will send a ```Ping``` message it receives from ```Pinger``` to ```PingServer```.

- ```PingerWorker``` will wait for a reply from ```PingServer``` for a specified amount of time: ```pingTimeout``` of type ```FiniteDuration```.

- If a reply is received in time, it is sent to ```Pinger```. Otherwise, ```PingerWorker``` throws an exception.

Implement the task in the following steps:

- A ```PingWorker``` actor class has been set-up already.
  - It defines an exception definition (```TimeoutException```).
  - It defines a case class ```Timedout``` to be used in the protocol.
  - Use the scheduler to send a message to ```self``` when the timeout period is completed.
  - Modify the behaviour as specified in the introduction.
  
- Modify ```Pinger``` as follows:
  - Let it create the appropriate number of ```PingerWorker``` actors and foreach of them, send the ```Ping``` (already scheduled in the code) to these actors.
  - Check the behaviour for other required modifications. 
  
Run the application and verify if it works as expected. Set the configuration parameters in such a way that a timeout is triggered and observe what happens. Is the observed behaviour the desired one ?

##Exercise 6 - Solution & discussion

When ```PingServer```'s reliability is set to a low level (e.g. 50), time-outs can be triggered easily. What we see in that case is that the default Akka SupervisoryStragegy is to restart a failed actor. This is clearly not what is needed: the ```PingerWorker``` actor should be stopped instead.

Let's correct this in the next exercise.

##Exercise 7 - Tuning an actor's supervision strategy

In this exercise you will define the appropriate supervisory strategy for the ```PingerWorker``` actor. Also, we want to correct the bookkeeping in the ```Pinger Actor``` - the actor who's supervising the ```PingerWorker```.

A new message ```PingTimedout``` message has been created to facilitate things: it can be sent to ```self``` to assist the bookkeeping process.

Your tasks:

- Implement the correct supervisory strategy for ```PingerWorker```
- Correct the bookkeeping so that the ```Pinger``` actor is stopped after having received all replies (or timeout indications).

##Exercise 7 - Solution & discussion

Note that the ```decider``` in actor ```Pinger``` *captures* the ```TimeoutException``` and specifies that the faulty actor should be stopped. Also, for bookkeeping purposes, it sends a ```PingTimedout``` message to itself which in turn leads to an update of the internal state of the actor.


## Exercise 8 - Using the Akka ask pattern

The ask pattern can be used to asynchronously get a response to a message sent to an actor.

We will use this to get the number of ```Pinger``` actors at a particular moment in time.

You will get this information from the ```PingMain``` actor by sending it a ```GetStatus``` (case object) defined in ```PingMain```. This actor should respond with a ```Status``` message holding the requested count. Hence, this should be reflected in the behaviour of the ```PingMain``` actor.

You will need to obtain the ActorRef for actor ```PingMain```. Have a look at ```ActorSelection``` in ```ActorSystem```.

Also note that ```ask``` needs the following in order to function:

- an (implicit) ```Timeout``` value.
- an (implicit) Execution context.

An ```ask``` will return a ```Future``` giving you a handle to a (future) result. Note that due to type erasure, you will need to apply ```mapTo``` to indicate the correct return type. Next, you'll finish things off by installing callbacks on it to handle successful completion or failure.

##Exercise 8 - Solution & discussion

Congrats - you made it !!!

##What's next ?

You've covered a lot of important features of Akka while solving the exercises in this hacking session. Do note however that there's a *lot* more to cover such as:

- Akka FSM & FSMLogging
- ```ask``` pattern in combination with ```pipeTo```
- Pooled router versus Group router & different routing strategies
- Supervisor strategies - ```OneForOneStrategy``` versus ```OneForAllStrategy```
- Akka persistence
- Local versus Remote deployment
- Akka clustering
- Akka sharding
- ....

##Suggestions for further exploration

You might extend ```AkkaNetworkPing``` by:

- Implementing retrying the sending of ```Ping``` messages in case no (```Response```) is replied within a certain time.
- Keep track of the number of ping requests, actual pings sent, number of timeouts.
- Report of the stats by extending the stats command
- Implement ```Pinger```, ```PingerWorker``` and ```PingServer``` using Akka FSM
- Adding more tests...
- Experiment with ```ask```/```pipeTo```
- ...

Some excellent Akka books to read:

- [Effective Akka by Jamie Allen](http://shop.oreilly.com/product/0636920028789.do)
- [Akka Concurrency by Derek Wyatt](http://www.artima.com/shop/akka_concurrency)
- [Learning Concurrent Programming in Scala by Aleksandar Prokopec](https://www.packtpub.com/application-development/learning-concurrent-programming-scala)


