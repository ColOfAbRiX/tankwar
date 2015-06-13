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

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction

import scala.collection.mutable.ListBuffer

/**
 * Hidden Layer with feedback
 *
 * Every time the function output is called, its return values are memorized and
 * fed into the feedback neurons to be used at the next call.
 *
 * @param activation The activation function used by the neurons
 * @param n_inputs The number of inputs for each neuron
 * @param n_outputs The number of outputs, which equals the number of neurons
 * @param _biases The set of bias values, one for each neuron
 * @param _weights The set of input weights. Every neuron has n_inputs weights.
 * @param remember Determines if the layer has to remember the output of every call
 * @param contextWeights The weights for the feedback. It is structured as `weights` and the inputs are as `n_output`
 */
class ElmanFeedbackLayer(
  activation: ActivationFunction,
  n_inputs: Int,
  n_outputs: Int,
  private val _biases: Seq[Double],
  private val _weights: Seq[Seq[Double]],
  protected val contextWeights: Seq[Seq[Double]],
  var remember: Boolean = true )
  extends HiddenLayer(activation, n_inputs, n_outputs, _biases, _weights) {

  // Check that every sequence of feedback weights associated with each neuron is the same size of the inputs of that neuron
  require(contextWeights.length == n_outputs && contextWeights.forall(_.length == n_outputs), "The size of context weights must match n_output")

  // To provide a uniform access to the data, the context weights are included in normal weights and the biases adjusted
  override val biases = _biases ++ Seq.fill(contextWeights.length)(0.0)
  override val weights = _weights ++ contextWeights

  /**
   * Contains the values used as inputs for the last feedback
   *
   * @return
   */
  def lastFeedback = _memory.toList

  // This memory contains the outputs of the previous call of output
  private val _memory: ListBuffer[Double] = ListBuffer.fill(n_outputs)(0.0)

  // This is actually done because want to trick the HiddenLayer and give it modified parameters instead of create
  // a new type of layer from scratch. I just inherit from HiddenLayer to have an external interface.
  // The internal layer is configured to accept the normal inputs plus the feedback inputs. In this way it's
  // also possible to have nested ElmanLayers
  private val internalLayer = new HiddenLayer(
    activation,
    n_inputs + n_outputs,
    n_outputs,
    _biases,
    mixInputs(_weights, contextWeights)
  )

  /**
   * It mixes two input sequences
   *
   * A sequence represents the inputs for all the neurons. As that, to mix inputs, it is
   * necessary to add together the second level of the sequence
   *
   * @param inputs1 First input sequence, structured as the weight variable
   * @param inputs2 Second input sequence, structured as the weight variable
   * @return A new `Seq[Seq[Double]]` containing, for each neuron, the inputs of the first lists and the inputs of the second list
   */
  private def mixInputs( inputs1: Seq[Seq[Double]], inputs2: Seq[Seq[Double]] ) =
    (inputs1 zip inputs2) map { case (i1, i2) => i1 ++ i2 }

  /**
   * Calculate the output of the layer
   *
   * Given a set of input values it calculates the set of output values
   *
   * @param inputs A sequence of double to feed the layer
   * @return A sequence of double representing the output
   */
  override def output( inputs: Seq[Double] ): Seq[Double] = {
    val outputs = internalLayer.output(inputs ++ _memory)

    if( remember ) {
      _memory.clear()
      _memory ++= outputs
    }

    outputs
  }
}
