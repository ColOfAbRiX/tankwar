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

package com.colofabrix.scala.neuralnetwork.old.builders

import com.colofabrix.scala.neuralnetwork.old.FeedforwardNeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.{ BehaviourBuilder, LayerReader }
import com.colofabrix.scala.neuralnetwork.old.layers.{ HiddenLayer, InputLayer, OutputLayer }

/**
 * Builder to create the components of a simple FeedForward Neural Network
 */
class FeedforwardBuilder extends BehaviourBuilder {
  /**
   * Builds a Neural Network
   *
   * @param inputLayer Input layer of the new Neural Network
   * @param hiddenLayers Hidden layers of the new Neural Network
   * @param outputLayer Output layer of the new Neural Network
   * @return A new instance of a Neural Network of type Feedforward
   */
  override def buildNetwork( inputLayer: InputLayer, hiddenLayers: Seq[HiddenLayer], outputLayer: OutputLayer ): NeuralNetwork =
    new FeedforwardNeuralNetwork(
      inputLayer,
      hiddenLayers,
      outputLayer
    )

  /**
   * Returns a new InputLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN
   */
  override def buildInputLayer( nInputs: Int, dataReader: LayerReader ): InputLayer =
    new InputLayer( nInputs )

  /**
   * Returns a new HiddenLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @param nNeurons The number of neurons in the HiddenLayer
   * @return A single HiddenLayer
   */
  override def buildHiddenLayer( nInputs: Int, nNeurons: Int, dataReader: LayerReader ): HiddenLayer =
    new HiddenLayer(
      dataReader.activationFunction,
      nInputs,
      nNeurons,
      dataReader.neuronBiases( nNeurons ),
      dataReader.inputWeights( nNeurons, nInputs )
    )

  /**
   * Returns a new OutputLayer for the specific BehaviourBuilder
   *
   * @param nInputs The number of inputs of the NN
   * @param nNeurons The number of neurons in the HiddenLayer
   * @return A single OutputLayer
   */
  override def buildOutputLayer( nInputs: Int, nNeurons: Int, dataReader: LayerReader ): OutputLayer =
    new OutputLayer(
      dataReader.activationFunction,
      nInputs,
      nNeurons,
      dataReader.neuronBiases( nNeurons ),
      dataReader.inputWeights( nNeurons, nInputs )
    )
}