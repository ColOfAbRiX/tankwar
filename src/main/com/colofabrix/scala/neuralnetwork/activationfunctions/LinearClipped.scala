package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**s
  * Linear Clipped activation function
  *
  * Maps the input directly to the output only between -1.0 and 1.0, then it fixes the output to one of those values
  */
class LinearClipped extends ActivationFunction {

  override val UFID = "ded7e1db-ac21-4812-b7b1-c66d02e392cb"

  override def apply(input: Double): Double = Math.max(Math.min(input, 1.0), -1.0)

  override def toString = "LinearClipped"

}