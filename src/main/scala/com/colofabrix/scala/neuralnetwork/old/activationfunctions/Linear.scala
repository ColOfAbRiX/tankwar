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
  * s
  * Linear function
  * Maps the input directly to the output
  */
class Linear extends ActivationFunction {

  override val UFID = "df8d5f6e-0523-4f60-90da-a9222ac8647a"

  override def apply( input: Double ): Double = input

  override def toString = "Linear"

}