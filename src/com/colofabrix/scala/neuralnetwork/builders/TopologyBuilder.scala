package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, StructureBuilder}
import com.colofabrix.scala.neuralnetwork.layers._

import scala.util.Random

/**
 * Creates a Three-Layer topology for a Neural Network
 * 
 * @param data The data reader
 * @param nHidden The number of neurons in the hidden layer
 * @param activation The activation function of the hidden layer
 */
class ThreeLayerNetwork(
  data: DataReader,
  nHidden: Int,
  activation: String = "tanh"
)
extends StructureBuilder {
  /**
   * Returns a sequence with one Hidden Layer
   *
   * @param nInitialInputs The number of inputs of the NN
   * @return A sequence containing a single HiddenLayer
   */
  override def hiddenLayers(nInitialInputs: Int) = List(new HiddenLayer(
    data.layerReaders(1).activationFunction,
    nInitialInputs,
    nHidden,
    data.layerReaders(1).neuronBiases(nHidden),
    data.layerReaders(1).inputWeights(nHidden, nInitialInputs)
  ))

  /**
   * Returns the output layer
   *
   * @param nOutputs The number of outputs of the NN
   * @return A new OutputLayer
   */
  override def outputLayer(nOutputs: Int) = new OutputLayer(
    data.layerReaders(2).activationFunction,
    nHidden,
    nOutputs,
    data.layerReaders(2).neuronBiases(nOutputs),
    data.layerReaders(2).inputWeights(nOutputs, nHidden)
  )
}


/**
 * Three-Layer topology with randomly initialized data
 *
 * @param nHidden The number of neurons in the hidden layer
 * @param activation The activation function of the hidden layer
 * @param scaling Scaling Factor. See `RandomReader`
 * @param rng Random number generator
 */
class RandomThreeLayerNetwork (
  nHidden: Int,
  scaling: Double = 1.0,
  activation: String = "tanh",
  rng: Random = new Random )
extends ThreeLayerNetwork(
  new RandomReader(scaling, 3, rng),
  nHidden,
  activation
)