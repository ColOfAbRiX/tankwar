package com.colofabrix.scala.neuralnetwork.builders.abstracts

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork
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
   * Build a Neural Network
   *
   * @param nInputs Number of inputs of the network
   * @param nOutputs Number of outputs of the network
   * @param dataReader Data reader used to create the network
   * @return A new Neural Network
   */
  def buildNetwork(nInputs: Int, nOutputs: Int, dataReader: DataReader): NeuralNetwork

  /**
   * Returns the input layer, usually the default is pass-through
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN. It defaults to `InputLayer`
   */
  protected def inputLayer(nInputs: Int, dataReader: DataReader): InputLayer

  /**
   * Returns the list of hidden layers
   *
   * @param nInitialInputs The number of inputs of the NN
   * @return A sequence of HiddenLayer
   */
  protected def hiddenLayers(nInitialInputs: Int, dataReader: DataReader): Seq[HiddenLayer]

  /**
   * Returns the output layer
   *
   * @param nOutputs The number of outputs of the NN
   * @return A new OutputLayer
   */
  protected def outputLayer(nOutputs: Int, dataReader: DataReader): OutputLayer

  /**
   * The number of hidden layers
   *
   * @return An integer indicating how many hidden layer this `StructureBuilder` will create
   */
  def hiddenLayersCount: Int

  /**
   * Builder for the behaviours of the single elements that can be built
   *
   * @return The BehaviourBuilder used to generated the elements of the structure
   */
  def behaviourBuilder: BehaviourBuilder
}
