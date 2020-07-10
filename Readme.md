## akka-essentials
This repository contains the code we wrote during Rock the JVM's Akka Essentials course.
https://udemy.com/akka-essentials

official repo: https://github.com/rockthejvm/udemy-akka-essentials


## how akka works
Akka has a thread pool that shares with actors. 
Thread - active can run code.
Actor - passive just a data structure, needs a thread. 
Actor has message handler.   receive method.
Actor has a message queue   mailbox .
Akka spawns a few threads (100s) and a lot of actors (1000000s)
Akka schedule actors for execution.
Mechanisms:
Sending message:message is enqueued into mailbox of actor. Its thread safe.
Processing message: a thread is scheduled (it will occupy actor) to run this actor. Actor may change his state.messages are extracted from mailbox in order.
At some point the actor is unscheduled in which point actor is released, thread will move on.
Guarantees: Only one thread per actor at any time. Effectively single threaded. No locks needed.
Message delivery guarantee: At most once delivery. for any sender receiver pair, message order is maintained.

## actor lifecycle
1. actor instance
  - has methods, internal state
2. actor reference
  - incarnation, actorOf, encapsuleted, mailbox, UUID, one actor instance
3. actor paths
  - may or may not have instance ActorRef inside
  
 - actors can be started, suspendend, resumed, restarted, stoppped.
 
 
## actor supervision
##
 
