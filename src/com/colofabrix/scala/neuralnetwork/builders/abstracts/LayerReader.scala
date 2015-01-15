package com.colofabrix.scala.neuralnetwork.builders.abstracts

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
 * Created by Fabrizio on 14/01/2015.
 */
trait LayerReader {
  def neuronBiases(neurons: Int): Seq[Double]

  def inputWeights(neurons: Int, inputs: Int): Seq[Seq[Double]]

  def activationFunction: ActivationFunction
}
