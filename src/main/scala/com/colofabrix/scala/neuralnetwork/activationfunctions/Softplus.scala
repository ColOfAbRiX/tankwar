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

/**
 * Library for Neural Networks
 *
 * Created by Fabrizio on 20/11/2014.
 */
package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
 * Softplus activation function
 *
 * @see https://en.wikipedia.org/wiki/Rectifier_%28neural_networks%29
 */
class Softplus extends ActivationFunction {

  override val UFID = "8772e26c-fe70-483c-96a8-dc4a2aa2f900"

  override def apply( input: Double ): Double = Math.log1p( Math.exp( input ) )

  override def toString = "Softplus"

}