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

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.{ DataReader, LayerReader }

/**
 * Sequence Data Reader
 *
 * It reads the data of a NN Layer from its sequences
 *
 * @param biases Biases of the NN
 * @param weights Weights of the NN
 */
class SeqDataReader( biases: Seq[Seq[Double]], weights: Seq[Seq[Seq[Double]]], af: Seq[String] ) extends DataReader {
  val combined = ( biases zip weights ).zipWithIndex

  // These checks are done because we need a reliable set of data to extract information like the number of inputs
  require( biases.length == weights.length, "Biases and weights must represent the same number of layers" )
  require( biases.nonEmpty, "At least one layer must be specified" )
  require( af.length == biases.length, "The activation functions must be specified for every layer" )
  for ( ( b, w ) ← biases zip weights ) {
    require( b.nonEmpty && b.length == w.length, "Bias and weights count must match the same number of neurons" )
    require( w.forall( w( 0 ).length == _.length ), "All the weights must be for the same number of inputs" )
  }

  override def layerReaders = combined map {
    case ( ( b, w ), i ) ⇒
      new SeqLayerReader( b, w, af( i ) )
  }
}

/**
 * Sequence Data Reader for a single Layer
 *
 * It reads the data of a Layer from a Sequence
 *
 * @param af Activation Function
 * @param biases Biases of the layer
 * @param weights Weights of the layer
 */
class SeqLayerReader( biases: Seq[Double], weights: Seq[Seq[Double]], af: String ) extends LayerReader {
  require( af != null, "An activation function must be specified" )

  /**
   * Returns the biases of the neurons
   *
   * @param neurons Number of neurons in the layer
   * @return The sequence of biases, one for each neuron
   */
  override def neuronBiases( neurons: Int ): Seq[Double] = {
    require( neurons > 0, "The number of neurons must be a positive integer" )
    //require(biases.length == neurons, "The number of biases don't match the number of neurons")

    biases
  }

  /**
   * Return the weights, for each neuron, associated with the inputs
   *
   * @param neurons Number of neurons in the layer
   * @param inputs Number of inputs for each neuron
   * @return The weights, for each neuron, associated with the inputs
   */
  override def inputWeights( neurons: Int, inputs: Int ): Seq[Seq[Double]] = {
    require( neurons > 0 )
    require( inputs > 0 )
    require( weights.length == neurons )
    require( weights( 0 ).length == inputs )

    weights
  }

  /**
   * Returns the ActivationFunction associated with the layer
   *
   * @return The ActivationFunction associated with the layer
   */
  override def activationFunction: ActivationFunction = ActivationFunction( af )
}