package com.colofabrix.scala.neuralnetworkOld.layers

import com.colofabrix.scala.neuralnetworkOld.abstracts.NeuronLayer
import com.colofabrix.scala.neuralnetworkOld.activationfunctions.Linear

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