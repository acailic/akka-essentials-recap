package part1recap

import scala.concurrent.Future

object AdvancedRecap extends App {

  // partial functions
  // works for some values , other throws an exception
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 23
    case 2 => 45
    case 3 => 111
  }

  val pf = (x: Int) => x match {
    case 1 => 23
    case 2 => 45
    case 3 => 111
  }

  val function: (Int => Int) = partialFunction

  val modifiedList = List(1, 2, 3).map {
    case 1 => 23
    case _ => 0
  }

  //lifting

  val lifted = partialFunction.lift // total function Int => Option[Int]
  lifted(2) // some(45)
  lifted(50000) /// none

  //orElse
  val pfChain = partialFunction.orElse[Int, Int] {
    case 22 => 9000
  }

  pfChain(3) /// 111
  pfChain(22) /// 9000
  pfChain(123123) /// throw match error

  //type aliases
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case 1 => println("hello")
    case _ => println("confused.......")
  }

  // implicits
  implicit val timeout = 3000

  def setTimeout(f: () => Unit)(implicit timeout: Int) = f()

  setTimeout(() => println("timeout")) // extra parameter list omitted

  //implicit conversions
  // 1) case defs
  case class Person(name: String) {
    def great = "Hi, my name is $name"
  }

  implicit def fromStringToPerson(string: String): Person = Person(string)

  // how it works - fromStringToPerson("Peter").greet -- done by compiler
  "Peter".great

  // 2) implicit clasess
  implicit class Dog(name: String) {
    def bark = println("aavavavavav!!!")
  }
  "Cuko".bark
  // new Dog("Lassie").bark  -- done by compiler


  /// implicits can confuse
  //local scope
  implicit val inverseOrdering: Ordering[Int]= Ordering.fromLessThan(_>_)
  List(1,2,3).sorted  // List(3,2,1) implicit value of this function is used because of scope

  //imported scope

  //sets scope for implicit values
  // 1 local 2 imported 3 companion objects
   import scala.concurrent.ExecutionContext.Implicits.global

  //future - no implicits found for execution context
  val future = Future {
    println("Hello future")
  }


  // companion objects of the type included in the call
  object Person{
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a,b)=> a.name.compareTo(b.name) < 0)
  }
  // alphabetic sorted
    //will use ordering from companion object
  List(Person("Alice"),Person("Bob")).sorted


}
