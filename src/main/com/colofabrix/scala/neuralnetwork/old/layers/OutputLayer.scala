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
class OutputLayer (
  activation: ActivationFunction,
  n_inputs: Int, n_outputs: Int,
  biases: Seq[Double], weights: Seq[Seq[Double]] )
extends NeuronLayer(
  activation,
  n_inputs, n_outputs,
  biases, weights
)