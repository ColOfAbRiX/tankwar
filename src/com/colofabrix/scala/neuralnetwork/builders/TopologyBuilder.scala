package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, TopologyBuilder}
import com.colofabrix.scala.neuralnetwork.layers._

import scala.util.Random

class ThreeLayerNetwork(
  data: DataReader,
  n_hidden: Int,
  activation: String = "tanh"
)
extends TopologyBuilder {
  private val _layerReaders = (1 to 3) map { _ => data.nextLayerReader }

  override def inputLayer(nInputs: Int) = new InputLayer(nInputs)

  override def hiddenLayers(nInitialInputs: Int) = List(new HiddenLayer(
    _layerReaders(1).activationFunction,
    nInitialInputs,
    n_hidden,
    _layerReaders(1).neuronBiases(n_hidden),
    _layerReaders(1).inputWeights(n_hidden, nInitialInputs)
  ))

  override def outputLayer(nOutputs: Int) = new OutputLayer(
    _layerReaders(2).activationFunction,
    n_hidden,
    nOutputs,
    _layerReaders(2).neuronBiases(nOutputs),
    _layerReaders(2).inputWeights(nOutputs, n_hidden)
  )
}

class RandomThreeLayerNetwork (
  n_hidden: Int,
  scaling: Double = 1.0,
  activation: String = "tanh",
  rng: Random = new Random )
extends ThreeLayerNetwork(
  new RandomReader(scaling, rng),
  n_hidden,
  activation
)