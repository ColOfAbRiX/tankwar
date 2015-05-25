package com.colofabrix.scala.neuralnetwork.old.activationfunctions

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction

/**s
  * Linear function
  * Maps the input directly to the output
  */
class LinearClipped extends ActivationFunction {

  override val UFID = "ded7e1db-ac21-4812-b7b1-c66d02e392cb"

  override def apply(input: Double): Double = Math.max(Math.min(input, 1.0), -1.0)

  override def toString = "LinearClipped"

}