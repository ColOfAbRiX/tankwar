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

package com.colofabrix.scala.neuralnetwork.old.layers

import com.colofabrix.scala.neuralnetwork.old.abstracts.{ActivationFunction, NeuronLayer}

/**
 * It represents the output layer of a NN
 *
 * An output layer is exactly a layer. This distinction has been made to put
 * constraints on the creation of a `@see GenericNeuralNetwork`.
 *
 * @param activation The activation function used by the neurons
 * @param n_inputs The number of inputs for each neuron
 * @param n_outputs The number of outputs, which equals the number of neurons
 * @param biases The set of bias values, one for each neuron
 * @param weights The set of input weights. Every neuron has n_inputs weights.
 */
class HiddenLayer(
  activation: ActivationFunction,
  n_inputs: Int, n_outputs: Int,
  biases: Seq[Double], weights: Seq[Seq[Double]] )
  extends NeuronLayer(
    activation,
    n_inputs, n_outputs,
    biases, weights
  )