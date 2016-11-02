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

package com.colofabrix.scala.neuralnetwork.old

import com.colofabrix.scala.neuralnetwork.old.layers.{ ElmanFeedbackLayer, InputLayer, OutputLayer }

/**
  * Elman Neural Network
  *
  * A type of Recurrent Neural Network
  */
class ElmanNeuralNetwork(
  override val input_layer: InputLayer,
  hidden_layers: Seq[ElmanFeedbackLayer],
  override val output_layer: OutputLayer,
  private var _rememberPast: Boolean = true
)
    extends FeedforwardNeuralNetwork(
      input_layer,
      hidden_layers,
      output_layer
    ) {

  def rememberPast = _rememberPast

  def rememberPast_=( value: Boolean ): Unit = {
    _rememberPast = value
    hidden_layers.foreach( _.remember = value )
  }

  override val weights = for ( layer ← all_layers ) yield layer.weights
}