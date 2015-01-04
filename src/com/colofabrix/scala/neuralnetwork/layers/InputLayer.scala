package com.colofabrix.scala.neuralnetwork.layers

import com.colofabrix.scala.neuralnetwork.abstracts.{ActivationFunction, NeuronLayer}
import com.colofabrix.scala.neuralnetwork.activationfunctions.Linear

/**
 * It represents the input layer of a NN
 *
 * An input layer is a layer that maps every input to the output providing an effective way
 * to use a uniform algorithm for all the layers.
 * It is represented by a layer with exactly one input per neuron, all weights = 1.0
 * associated to them and an activation function that maps every input to the output.
 * This is the default implementation but it can be used to condition the inputs to certain
 * values
 *
 * @param n_inputs The number of inputs for the whole NN
 */
class InputLayer( n_inputs: Int )
extends NeuronLayer(
  new Linear,
  n_inputs,
  n_inputs,
  Seq.fill(n_inputs)(0.0),
  Seq.tabulate(n_inputs, n_inputs)( (i, j) => if( i == j ) 1.0 else 0.0 )   // Kronecker delta
)

/**
 * It represents the input layer of a NN with advanced option that an `InputLayer`
 *
 * TODO: Check that the given parameters are not overridden by the constructor of InputLayer
 *
 * @param activation The activation function used by the neurons
 * @param n_inputs The number of inputs for each neuron
 * @param n_outputs The number of outputs, which equals the number of neurons
 * @param biases The set of bias values, one for each neuron
 * @param weights The set of input weights. Every neuron has n_inputs weights.
 */
class ExtendedInputLayer (
  override val activation: ActivationFunction,
  n_inputs: Int,
  override val n_outputs: Int,
  override val biases: Seq[Double],
  override val weights: Seq[Seq[Double]] )
extends InputLayer(
  n_inputs
)