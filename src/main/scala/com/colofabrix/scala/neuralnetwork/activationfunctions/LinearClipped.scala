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
 * s
 * Linear Clipped activation function
 *
 * Maps the input directly to the output only between -1.0 and 1.0, then it fixes the output to one of those values
 */
class LinearClipped extends ActivationFunction {

  override val UFID = "ded7e1db-ac21-4812-b7b1-c66d02e392cb"

  override def apply( input: Double ): Double = Math.max( Math.min( input, 1.0 ), -1.0 )

  override def toString = "LinearClipped"

}