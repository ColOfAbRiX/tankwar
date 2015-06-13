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
 * Sin function
 * See https://en.wikipedia.org/wiki/Sin
 */
class Sin extends ActivationFunction {

  override val UFID = "2ba7737f-f16f-4993-820c-a51b08e7082e"

  override def apply(input: Double): Double = math.tanh(input)

  override def toString = "Sin"

}