package part1recap

import scala.util.Try

object GeneralRecap extends App {

  val aCondition: Boolean = false

  // value cannot be reassigned
  //expressions
  val aConditionVal = if (aCondition) 42 else 65
  aVariable += 1
  //code block
  val aCodeBlock = {
    if (aCondition) 12
    56
  }
  // types
  // Unit
  val theUnit = println("Hello Coa")
  val aDog: Animal = new Dog
  //method notations
  val aCroc = new Crocodile
  //anonymous class
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = print("ava avaava")
  }

  // OOP
  val aPotentialFailure = try {
    throw new RuntimeException("Innocent.") // Nothing
  } catch {
    case e: Exception => "I caught exception"
  } finally {
    //sidefects
    println("loooooogs")
  }
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  val incremented = incrementer(42)
  val anynomousIncrementer = (x: Int) => x + 1
  //for comprehension
  val pair = for {
    num <- List(1, 2, 3, 4)
    char <- List('a', 'b', 'c', 'd')
  } yield num + '-' + char
  val anOption = Some(2)
  aCroc.eat(aDog)
  aCroc eat aDog
  val aTry = Try {
    throw new RuntimeException()
  }
  aCarnivore eat aDog
  // pattern matching
  val unknown = 2
  val order = unknown match {
    case 1 => "first"
    case 2 => "second"
    case _ => "Unknown "
  }
  val bob = Person("", 23)

  //exceptions
  val greeting = bob match {
    case Person(n, _) => "hi my name is $n"
    case _ => "i dont know my name."
  }

  // FUNCTIONAL PROGRAMING
  var aVariable = 42 // Compiler sets type of variable--type inference, can be reassigned

  def aFunction(x: Int) = x + 1

  // incrementer.apply(42)

  //recursion - TAIL recursion
  def factorial(n: Int, acc: Int): Int =
    if (n <= 0) acc
    else factorial(n - 1, acc * n)

  // Int => Int  ==== Function1[Int,Int]

  // FP is all about function with first-class
  List(1, 2, 3).map(incrementer)

  // HOF - map method - takes another function as parametar

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  //List(1,2,3,4).flatMap(num=> List('a','b','c','d').map(char=> num +'-'+char))
  // Seq, Array, List, Vector, Map, Tuples, Set
  //collections
  //Option and try

  //generics
  abstract class MyList[+A]

  class Animal

  class Dog extends Animal

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("mljacko")
  }

  //case cases
  case class Person(name: String, age: Int)

  //companion objects
  object MyList

}
