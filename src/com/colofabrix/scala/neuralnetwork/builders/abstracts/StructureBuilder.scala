package com.colofabrix.scala.neuralnetwork.builders.abstracts

import com.colofabrix.scala.neuralnetwork.layers.{HiddenLayer, InputLayer, OutputLayer}

/**
 * Neural Network Structural Builder
 *
 * Neural networks are considered made of 3 parts:
 *  - A behaviour: feed-forward, Elman, Jordan, ...
 *  - A structure: 3-layers, perceptron, random, ...
 *  - Data: biases, weights, activation functions, ...
 *
 * A structure builder is used by a `BehaviourBuilder` to build the structure
 * of the Neural Network being constructed
 */
trait StructureBuilder {
  /**
   * Returns the input layer, usually the default is pass-through
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN. It defaults to `InputLayer`
   */
  def inputLayer(nInputs: Int, dataReader: DataReader): InputLayer = new InputLayer(nInputs)

  /**
   * Returns the list of hidden layers
   *
   * @param nInitialInputs The number of inputs of the NN
   * @return A sequence of HiddenLayer
   */
  def hiddenLayers(nInitialInputs: Int, dataReader: DataReader): Seq[HiddenLayer]

  /**
   * Returns the output layer
   *
   * @param nOutputs The number of outputs of the NN
   * @return A new OutputLayer
   */
  def outputLayer(nOutputs: Int, dataReader: DataReader): OutputLayer
}
