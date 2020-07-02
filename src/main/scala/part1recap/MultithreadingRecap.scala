package part1recap

import scala.concurrent.Future
import scala.util.{Failure, Success}

object MultithreadingRecap extends App {
  // creating threads in JVM
  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("im running in parallel")
  })

  val aThreadBetter = new Thread(() => println("Better written than above."))

  aThread.start()
  aThread.join() //wait to finish

  val threadHello = new Thread(() => (1 to 1000).foreach(_ => println("hello")))
  val threadBye = new Thread(() => (1 to 1000).foreach(_ => println("bye")))
  threadHello.start()
  threadBye.start()

  // different runs produce different results

  //making it thread safe
  //1 synchronized
  //2 volatile -- no right and write simult but for specific type
  class BankAccount(@volatile private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.amount -= money

    def safeWithdraw(money: Int) = this.synchronized {
      this.amount -= money
    }
  }

  // not thread safe .
  // NOT ATOMIC
  // adding synchronized blocks
  //

  // inter thread communication
  // wait and notify
  // scala future
  import scala.concurrent.ExecutionContext.Implicits.global

  var future = Future {
    // long computation - on a different thread
    32
  }

  // we can do callbacks
  future.onComplete {
    case Success(32) => println("Meaning of life .............")
    case Failure(_) => println("errrrrrorr  ")
  }
  /// future is ammoniated contract it has functional primitive

  val aProccesedFuture = future.flatMap {
    value => Future(value + 2)
  } // Future with 34


  val filteredFuture = future.filter(_ % 2 == 0) // if no elements  there is NoSuchElementException

  // for comprehension
  val aNonSenseFuture = for {
    meaningOfLife <- future
    filteredMeaning <- filteredFuture
  } yield meaningOfLife + filteredMeaning


  // andThen, recovery, recoverWith
  // Promises


}
