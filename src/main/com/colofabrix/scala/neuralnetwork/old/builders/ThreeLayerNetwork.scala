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

import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.{BehaviourBuilder, DataReader, StructureBuilder}
import com.colofabrix.scala.neuralnetwork.old.layers.InputLayer

/**
 * Creates a Three-Layer topology for a Neural Network
 *
 * A ThreeLayerNetwork will ignore any data for the InputLayer and it will
 * set it as the default implementation `InputLayer`
 *
 * @param behaviourBuilder Builder of the type of the Neural Network
 * @param nHidden The number of neurons in the hidden layer
 */
class ThreeLayerNetwork( override val behaviourBuilder: BehaviourBuilder, nHidden: Int ) extends StructureBuilder {
  /**
   * Build a Neural Network
   *
   * @param nInputs Number of inputs of the network
   * @param nOutputs Number of outputs of the network
   * @param dataReader Data reader used to create the network
   * @return A new Neural Network
   */
  override def buildNetwork( nInputs: Int, nOutputs: Int, dataReader: DataReader ): NeuralNetwork = {
    behaviourBuilder.buildNetwork(
      inputLayer(nInputs, dataReader),
      hiddenLayers(nInputs, dataReader),
      outputLayer(nOutputs, dataReader)
    )
  }

  /**
   * Returns the input layer, usually the default is pass-through
   *
   * @param nInputs The number of inputs of the NN
   * @return A new InputLayer for the NN. It defaults to `InputLayer`
   */
  override def inputLayer( nInputs: Int, data: DataReader ): InputLayer =
    behaviourBuilder.buildInputLayer(nInputs, data.layerReaders(0))

  /**
   * Returns a sequence with one Hidden Layer
   *
   * @param nInitialInputs The number of inputs of the NN
   * @return A sequence containing a single HiddenLayer
   */
  override def hiddenLayers( nInitialInputs: Int, data: DataReader ) = {
    require(data.layerReaders.length == 3, "The reader must be for a 3-layer network")

    List(
      behaviourBuilder.buildHiddenLayer(nInitialInputs, nHidden, data.layerReaders(1))
    )
  }

  /**
   * Returns the output layer
   *
   * @param nOutputs The number of outputs of the NN
   * @return A new OutputLayer
   */
  override def outputLayer( nOutputs: Int, data: DataReader ) = {
    require(data.layerReaders.length == 3, "The reader must be for a 3-layer network")
    behaviourBuilder.buildOutputLayer(nHidden, nOutputs, data.layerReaders(2))
  }

  /**
   * The number of hidden layers
   *
   * @return An integer indicating how many hidden layer this `StructureBuilder` will create
   */
  override def hiddenLayersCount: Int = 3
}