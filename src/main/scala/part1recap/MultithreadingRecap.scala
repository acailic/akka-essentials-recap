package part1recap

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

  class BankAccount(private var amount: Int){
    override def toString: String = ""+amount
    def withdraw (money: Int) =this.amount -= money
  }

  // not thread safe .



}
