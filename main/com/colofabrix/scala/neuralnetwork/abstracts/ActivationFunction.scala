package com.colofabrix.scala.neuralnetwork.abstracts

import com.colofabrix.scala.neuralnetwork.activationfunctions._

/**
 * Abstract definition of a Neural Network Activation Function
 */
abstract trait ActivationFunction {
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
   * @return
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

  def apply(name: String) = name.toLowerCase match {
    case "linear" => new Linear
    case "clipped" => new LinearClipped
    case "step" => new Step
    case "sigmoid" => new Sigmoid
    case "tanh" => new Tanh
    case "softplus" => new Softplus
    case "rectifier" => new Rectifier
    case "sin" => new Sin
    case _ => throw new IllegalArgumentException
  }

}