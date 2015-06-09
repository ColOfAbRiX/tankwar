package com.colofabrix.scala.neuralnetwork.abstracts

/**
 * Provides a way do test a Neural Network
 *
 * Neural networks are complex systems and it's difficult to study or predict their behaviour. This trait aims at
 * providing a way to study the network itself.
 * The tests can be any kind of test, like a dump on CVS or performance tests.
 */
trait NetworkAnalyser {

  /**
   * Run the tests for the network
   */
  def runTests(): Unit

}
