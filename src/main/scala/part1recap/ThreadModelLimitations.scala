package part1recap

import scala.concurrent.Future

object ThreadModelLimitations extends App {

  /* 1.
  OOP encapsulation works only valid in single threaded model
   */

  class BankAcount(private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.synchronized {
      this.amount -= money
    }

    def deposit(money: Int) = this.synchronized {
      this.amount += money
    }

    def getAmount =  amount
  }

  /*
    var account = new BankAcount(2222)
    for (_ <- 1 to 10000) {
      new Thread(() => account.withdraw(1)).start()
    }

    for (_ <- 1 to 10000) {
      new Thread(() => account.deposit(1)).start()
    }

    println(account.getAmount)*/

  // OOP encapsulation is broken in multithreaded env
  // synchronization - way to control
  // introduce deadlocks, livelocks
  // --------------------------------------------------------------------------

  /* 2.
    Delegating  something to thread is in PAIN
  */
  /// running thread and you want to pass a runnable to that thread .
  var task: Runnable = null

  val runningThread: Thread = new Thread(() => {
    while (true) {
      while (task == null) {
        runningThread.synchronized {
          println("[background]---waiting for task background.....")
          runningThread.wait()
        }
      }

      task.synchronized {
        println("[background]---i have a task")
        task.run()
        task = null
      }
    }
  })


  def delegateToBackgroundThread(r: Runnable) {
    if (task == null) task = r
    runningThread.synchronized {
      runningThread.notify()
    }
  }

  runningThread.start()
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println(23))
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println(" this should run in the background "))



  /*  3.
    Delegate tracing dealing with errors in a multithreaded env
   */
  // 1 milion numbers in 10 threads
  import scala.concurrent.ExecutionContext.Implicits.global
  val  futures = (0 to 9)
    .map(i=> 100000* i  until 100000 * (i +1)) //0-99999, 100000-199999, 200000-299999
    .map(range => Future {
        if(range.contains(546735)) throw new RuntimeException("invalid number")  // example so it fails one future
        range.sum

  })

  val sumFuture = Future.reduceLeft(futures)(_+_)  //Future  with sum  of all numbers
  sumFuture.onComplete(println)

}
