/**
 * Library for Neural Networks
 *
 * Created by Fabrizio on 20/11/2014.
 */
package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
 * Softplus activation function
 *
 * @see https://en.wikipedia.org/wiki/Rectifier_%28neural_networks%29
 */
class Softplus extends ActivationFunction {

  override val UFID = "8772e26c-fe70-483c-96a8-dc4a2aa2f900"

  override def apply(input: Double): Double = Math.log( 1 + Math.exp(input) )

  override def toString = "Softplus"

}