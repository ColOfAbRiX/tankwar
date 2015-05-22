package com.colofabrix.scala.neuralnetworkOld.builders.abstracts

import com.colofabrix.scala.neuralnetworkOld.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetworkOld.layers.{HiddenLayer, InputLayer, OutputLayer}

/**
 * Neural Network Behaviour Builder
 *
 * Neural networks are considered made of 3 parts:
 *  - A behaviour: feed-forward, Elman, Jordan, ...
 *  - A structure: 3-layers, perceptron, random, ...
 *  - Data: biases, weights, activation functions, ...
 *
 * A behaviour is used directly by the client to construct its own NN and
 * which can know only the number of inputs it provides and the number of
 * output it needs
 */
trait BehaviourBuilder {
  /**
   * Builds a Neural Network
   *
   * @param inputLayer Input layer of the new Neural Network
   * @param hiddenLayers Hidden layers of the new Neural Network
   * @param outputLayer Output layer of the new Neural Network
   * @return A new instance of a Neural Network
   */
  def buildNetwork(inputLayer: InputLayer, hiddenLayers: Seq[HiddenLayer], outputLayer: OutputLayer): NeuralNetwork

  /**
   * Returns a new InputLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN
   */
  def buildInputLayer(nInputs: Int, dataReader: LayerReader): InputLayer

  /**
   * Returns a new HiddenLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @param nNeurons The number of neurons in the HiddenLayer
   * @return A single HiddenLayer
   */
  def buildHiddenLayer(nInputs: Int, nNeurons: Int, dataReader: LayerReader): HiddenLayer

  /**
   * Returns a new OutputLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @param nNeurons The number of neurons in the HiddenLayer
   * @return A single OutputLayer
   */
  def buildOutputLayer(nInputs: Int, nNeurons: Int, dataReader: LayerReader): OutputLayer
}