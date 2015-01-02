package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork._
import com.colofabrix.scala.neuralnetwork.layers._

import scala.util.Random

abstract class TopologyBuilder {
  def build: GenericNeuralNetwork
}

class Random3LNetwork (
  n_inputs: Int,
  n_hidden: Int,
  n_output: Int,
  scaling: Double = 1.0
)
extends TopologyBuilder {

  private def getRandom = Random.nextDouble * 2 * scaling - scaling

  override def build: GenericNeuralNetwork = {
    val inputLayer = new InputLayer(n_inputs)

    val hiddenLayer = new HiddenLayer(
      ActivationFunctions.tanh,
      n_inputs,
      n_hidden,
      Seq.fill(n_hidden)(getRandom),
      Seq.fill(n_hidden, n_inputs)(getRandom)
    )

    val outputLayer = new OutputLayer(
      ActivationFunctions.tanh,
      n_hidden,
      n_output,
      Seq.fill(n_output)(getRandom),
      Seq.fill(n_output, n_hidden)(getRandom)
    )

    new GenericNeuralNetwork(inputLayer, Seq(hiddenLayer), outputLayer)
  }
}