package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
  * Heaviside activation function
 *
  * @see https://en.wikipedia.org/wiki/Heaviside_step_function
  */
class Step extends ActivationFunction {

  override val UFID = "b24fdb05-3f4a-4c8d-bdab-3b3d031c93da"

  override def apply(input: Double): Double = if (input >= 0.0) 1.0 else 0.0

  override def toString = "Step"

}