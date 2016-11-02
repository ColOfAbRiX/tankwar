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

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction

/**
  * Data Reader
  *
  * It provides a structured access to the data of the Neural Network.
  * It works in a sequential fashion so that every call of `nextLayerReader` returns the current
  * `LayerReader` and moves forward an internal pointer to the next one.
  */
trait DataReader {
  /**
    * Gets the next `LayerReader` for the next layer.
    *
    * Every call to this method advances an internal pointer so that every call returns
    * another reader
    *
    * @return An instance of `LayerReader` in the current position
    */
  def layerReaders: Seq[LayerReader]
}

/**
  * It provides the data for a layer
  */
trait LayerReader {
  /**
    * Returns the biases of the neurons
    *
    * @param neurons Number of neurons in the layer
    * @return The sequence of biases, one for each neuron
    */
  def neuronBiases( neurons: Int ): Seq[Double]

  /**
    * Return the weights, for each neuron, associated with the inputs
    *
    * @param neurons Number of neurons in the layer
    * @param inputs Number of inputs for each neuron
    * @return The weights, for each neuron, associated with the inputs
    */
  def inputWeights( neurons: Int, inputs: Int ): Seq[Seq[Double]]

  /**
    * Returns the ActivationFunction associated with the layer
    *
    * @return The ActivationFunction associated with the layer
    */
  def activationFunction: ActivationFunction
}