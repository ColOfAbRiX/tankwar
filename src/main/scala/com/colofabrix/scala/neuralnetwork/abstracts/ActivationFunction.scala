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

import com.colofabrix.scala.neuralnetwork.activationfunctions._

/**
 * Abstract definition of a Neural Network Activation Function
 */
trait ActivationFunction {

  /**
   * Calculates a function over a number
   *
   * @param input Input value
   * @return The result of the applied function
   */
  def apply( input: Double ): Double

  /**
   * Unique Function Identifier
   *
   * This field is used to distinguish an Activation Function from another. It must be
   * unique between various activation functions. It is recommended to be a UUID
   *
   * @return An identifier string unique between the activation functions
   */
  def UFID: String

  /**
   * Check that two ActivationFunctions are the same function
   *
   * For this purpose the field `UFID` it is used
   *
   * @param that The other object to compare
   * @return true if the two objects represents the same activation function
   */
  final override def equals( that: Any ) = this.hashCode == that.hashCode

  /**
   * The hashCode of the instance
   *
   * @return An hashCode based only on the type of the class
   */
  final override def hashCode: Int = this.UFID.hashCode
}

object ActivationFunction {

  /**
   * Activation function factory
   *
   * @param name The type of activation function
   * @return A new activation function from the given string
   */
  def apply( name: String ) = name.toLowerCase match {
    case "linear" ⇒ new Linear
    case "clipped" ⇒ new LinearClipped
    case "step" ⇒ new Step
    case "sigmoid" ⇒ new Sigmoid
    case "tanh" ⇒ new Tanh
    case "softplus" ⇒ new Softplus
    case "rectifier" ⇒ new Rectifier
    case "sin" ⇒ new Sin
    case _ ⇒ throw new IllegalArgumentException
  }

}