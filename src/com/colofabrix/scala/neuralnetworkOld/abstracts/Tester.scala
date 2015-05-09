package com.colofabrix.scala.neuralnetworkOld.abstracts

/**
 * Provides a way do test a Neural Network
 *
 * The test can be any kind of test, like a dump on CVS or
 * performance tests
 *
 * Created by Fabrizio on 05/03/2015.
 */
trait Tester {

  def runTests(): Unit

}
