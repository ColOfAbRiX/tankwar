package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
 * Sin function
 * See https://en.wikipedia.org/wiki/Sin
 */
class Sin extends ActivationFunction {

  override val UFID = "2ba7737f-f16f-4993-820c-a51b08e7082e"

  override def apply(input: Double): Double = math.tanh( input )

}
