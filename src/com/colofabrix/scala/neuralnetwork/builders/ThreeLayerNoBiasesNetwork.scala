package com.colofabrix.scala.neuralnetwork.builders

import com.colofabrix.scala.neuralnetwork.builders.abstracts.{DataReader, StructureBuilder}
import com.colofabrix.scala.neuralnetwork.layers.{HiddenLayer, OutputLayer}

/**
 * Creates a Three-Layer topology for a Neural Network
 *
 * A ThreeLayerNetwork will ignore any data for the InputLayer and it will
 * set it as the default implementation `InputLayer`
 *
 * @param nHidden The number of neurons in the hidden layer
 * @param activation The activation function of the hidden layer
 */
class ThreeLayerNoBiasesNetwork(nHidden: Int, activation: String) extends StructureBuilder {
  /**
   * Returns a sequence with one Hidden Layer
   *
   * @param nInitialInputs The number of inputs of the NN
   * @return A sequence containing a single HiddenLayer
   */
  override def hiddenLayers(nInitialInputs: Int, data: DataReader) = {
    require(data.layerReaders.length == 3, "The reader must be for a 3-layer network")

    List(new HiddenLayer(
      data.layerReaders(1).activationFunction,
      nInitialInputs,
      nHidden,
      Seq.fill(nHidden)(0.0),
      data.layerReaders(1).inputWeights(nHidden, nInitialInputs)
    ))
  }

  /**
   * Returns the output layer
   *
   * @param nOutputs The number of outputs of the NN
   * @return A new OutputLayer
   */
  override def outputLayer(nOutputs: Int, data: DataReader) = {
    require(data.layerReaders.length == 3, "The reader must be for a 3-layer network")

    new OutputLayer(
      data.layerReaders(2).activationFunction,
      nHidden,
      nOutputs,
      Seq.fill(nOutputs)(0.0),
      data.layerReaders(2).inputWeights(nOutputs, nHidden)
    )
  }

  /**
   * The number of hidden layers
   *
   * @return An integer indicating how many hidden layer this `StructureBuilder` will create
   */
  override def hiddenLayersCount: Int = 3
}
