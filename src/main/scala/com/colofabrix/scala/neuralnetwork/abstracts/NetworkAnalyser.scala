/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

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
  def runTests( ): Unit

}