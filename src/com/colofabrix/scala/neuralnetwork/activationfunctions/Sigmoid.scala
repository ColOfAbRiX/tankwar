package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
  * Sigmoid function
  * See https://en.wikipedia.org/wiki/Sigmoid_function
  */
class Sigmoid extends ActivationFunction {

   override val UFID = "c39f0734-27bc-4dd2-bbbf-bb1128c06692"

   override def apply(input: Double): Double = 1.0 / (1 + Math.exp( - input ))

 }
