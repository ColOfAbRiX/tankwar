package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
  * Rectifier function
  * See https://en.wikipedia.org/wiki/Rectifier_%28neural_networks%29
  */
class Rectifier extends ActivationFunction {

   override val UFID = "66425fa2-3a75-4e7e-9ab2-70ca91590fb0"

   override def apply(input: Double): Double = Math.max( 0, input )

  override def toString = "Rectifier"

 }
