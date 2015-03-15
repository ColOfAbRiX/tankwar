package com.colofabrix.scala.neuralnetwork.abstracts

/**
 * Provides a way do test a Neural Network
 *
 * The test can be any kind of test, like a dump on CVS or
 * performance tests
 *
 * Created by Fabrizio on 05/03/2015.
 */
trait NNTester {

  def runTests(): Unit

}