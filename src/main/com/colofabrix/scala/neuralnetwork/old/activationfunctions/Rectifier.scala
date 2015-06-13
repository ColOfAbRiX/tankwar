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

package com.colofabrix.scala.neuralnetwork.old.activationfunctions

import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction

/**
  * Rectifier function
  * See https://en.wikipedia.org/wiki/Rectifier_%28neural_networks%29
  */
class Rectifier extends ActivationFunction {

   override val UFID = "66425fa2-3a75-4e7e-9ab2-70ca91590fb0"

   override def apply(input: Double): Double = Math.max( 0, input )

  override def toString = "Rectifier"

 }
