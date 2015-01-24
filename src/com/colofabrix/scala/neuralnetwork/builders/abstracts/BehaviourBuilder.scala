package com.colofabrix.scala.neuralnetwork.builders.abstracts

import com.colofabrix.scala.neuralnetwork.abstracts.NeuralNetwork

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
   * @param nInputs The number of input provided to the NN
   * @param nOutputs The number of outputs the NN returns
   * @return A new instance of a Neural Network
   */
  def build(nInputs: Int, nOutputs: Int, dataReader: DataReader): NeuralNetwork
}