/*
 * Copyright (C) 2017 Fabrizio Colonna
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

package com.colofabrix.scala.physix

import com.colofabrix.scala.math._

/**
  * Generic Physix Engine.
  */
trait PhysixEngine {
  type PhysixAction[+A] = scalaz.State[World, A]

  /** Calculate the initial values of last position and velocity. */
  def init(mass: Double, position: Vect, velocity: Vect): PhysixAction[(Vect, Vect)]

  /** Move one body one time step into the future. */
  def moveBody(body: RigidBody): PhysixAction[RigidBody]

  /** Advances the bodies of one step. */
  def step(): PhysixAction[Seq[RigidBody]]
}
