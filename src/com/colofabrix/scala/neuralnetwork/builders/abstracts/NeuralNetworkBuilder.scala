package com.colofabrix.scala.neuralnetwork.builders.abstracts

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork

/**
 * Created by Fabrizio on 14/01/2015.
 */
abstract class NeuralNetworkBuilder(topology: TopologyBuilder) {
  def build(nInputs: Int, nOutputs: Int): NeuralNetwork
}
