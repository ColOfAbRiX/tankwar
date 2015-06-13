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

import com.colofabrix.scala.neuralnetwork.old.abstracts.NeuronLayer
import com.colofabrix.scala.neuralnetwork.old.activationfunctions.Linear

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