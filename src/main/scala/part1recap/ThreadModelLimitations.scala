package part1recap

object ThreadModelLimitations extends App {

  /*
  OOP encapsulation works only valid in single threaded model
   */

  class BankAcount(private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int) = this.amount -= money

    def deposit(money: Int) = this.amount += money

    def getAccount = account
  }

  var account = new BankAcount(2222)
  for (_ <- 1 to 10000) {
    new Thread(() => account.withdraw(1)).start()
  }

  for (_ <- 1 to 10000) {
    new Thread(() => account.deposit(1)).start()
  }

  println(account.getAccount)

}
