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

package com.colofabrix.scala.neuralnetwork.old.abstracts

/**
  * Input helper object
  *
  * Is is used to provide a nice interface between the input of a `GenericNeuralNetwork`
  * (s Seq[T]) and the client. Using a companion object with arguments will bring much
  * cleaner code can create a tidy way to provide data to the `GenericNeuralNetwork`
  *
  * Created by Fabrizio on 07/01/2015.
  */
abstract class InputHelper[T] {

  /**
    * The sequence of values to feed into the NN
    */
  protected val _values: Seq[T]

  /**
    * Fetch the data from the object
    * @return The data to feed the NN
    */
  final def getValues: Seq[T] = _values

}

/**
  * Output helper object
  *
  * Is is used to provide a nice interface between the input of a `GenericNeuralNetwork`
  * (s Seq[T]) and the client. Specializing the class and adding field will bring much
  * cleaner code can create a tidy way to fetch data from the `GenericNeuralNetwork`
  *
  * Created by Fabrizio on 07/01/2015.
  */
abstract class OutputHelper[T]( val raw: Seq[T] )