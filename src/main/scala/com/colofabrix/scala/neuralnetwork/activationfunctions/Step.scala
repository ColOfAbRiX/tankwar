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

package com.colofabrix.scala.neuralnetwork.activationfunctions

import com.colofabrix.scala.neuralnetwork.abstracts.ActivationFunction

/**
  * Heaviside activation function
  *
  * @see https://en.wikipedia.org/wiki/Heaviside_step_function
  */
class Step extends ActivationFunction {

  override val UFID = "b24fdb05-3f4a-4c8d-bdab-3b3d031c93da"

  override def apply( input: Double ): Double = if ( input >= 0.0 ) 1.0 else 0.0

  override def toString = "Step"

}