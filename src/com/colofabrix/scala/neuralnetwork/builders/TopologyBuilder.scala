package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, StructureBuilder}
import com.colofabrix.scala.neuralnetwork.layers._

import scala.util.Random

class ThreeLayerNetwork(
  data: DataReader,
  n_hidden: Int,
  activation: String = "tanh"
)
extends StructureBuilder {

  override def hiddenLayers(nInitialInputs: Int) = List(new HiddenLayer(
    data.layerReaders(1).activationFunction,
    nInitialInputs,
    n_hidden,
    data.layerReaders(1).neuronBiases(n_hidden),
    data.layerReaders(1).inputWeights(n_hidden, nInitialInputs)
  ))

  override def outputLayer(nOutputs: Int) = new OutputLayer(
    data.layerReaders(2).activationFunction,
    n_hidden,
    nOutputs,
    data.layerReaders(2).neuronBiases(nOutputs),
    data.layerReaders(2).inputWeights(nOutputs, n_hidden)
  )
}

class RandomThreeLayerNetwork (
  n_hidden: Int,
  scaling: Double = 1.0,
  activation: String = "tanh",
  rng: Random = new Random )
extends ThreeLayerNetwork(
  new RandomReader(scaling, 3, rng),
  n_hidden,
  activation
)