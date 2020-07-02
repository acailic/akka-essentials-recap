package part1recap

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



}
