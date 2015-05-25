package com.colofabrix.scala.neuralnetwork.old.activationfunctions

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction

/**
  * Hyperbolic tangent function
  * See https://en.wikipedia.org/wiki/Hyperbolic_function
  */
class Tanh extends ActivationFunction {

  override val UFID = "183302bf-811f-4c31-bac0-860738251023"

  override def apply(input: Double): Double = math.tanh( input )

  override def toString = "Tanh"

}
