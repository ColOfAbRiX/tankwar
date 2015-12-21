/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.scala.neuralnetwork.old.builders.abstracts

import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.layers.{ HiddenLayer, InputLayer, OutputLayer }

/**
 * Neural Network Structural Builder
 *
 * Neural networks are considered made of 3 parts:
 * - A behaviour: feed-forward, Elman, Jordan, ...
 * - A structure: 3-layers, perceptron, random, ...
 * - Data: biases, weights, activation functions, ...
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
  def buildNetwork( nInputs: Int, nOutputs: Int, dataReader: DataReader ): NeuralNetwork

  /**
   * Returns the input layer, usually the default is pass-through
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN. It defaults to `InputLayer`
   */
  protected def inputLayer( nInputs: Int, dataReader: DataReader ): InputLayer

  /**
   * Returns the list of hidden layers
   *
   * @param nInitialInputs The number of inputs of the NN
   * @return A sequence of HiddenLayer
   */
  protected def hiddenLayers( nInitialInputs: Int, dataReader: DataReader ): Seq[HiddenLayer]

  /**
   * Returns the output layer
   *
   * @param nOutputs The number of outputs of the NN
   * @return A new OutputLayer
   */
  protected def outputLayer( nOutputs: Int, dataReader: DataReader ): OutputLayer

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