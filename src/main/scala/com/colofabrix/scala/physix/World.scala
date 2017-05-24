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

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math._

/**
  * Generic definition of a World
  */
trait World {
  /** The force field present on the arena, point by point */
  def forceField: VectorField

  /** The friction present on the arena, point by point */
  def friction(body: RigidBody): ScalarField

  /** The walls of the world. */
  def walls: Seq[Shape]

  /** List of the bodies in the World. */
  def bodies: Seq[RigidBody]

  /** The time delta from the previous step. */
  def timeDelta: Double

  def copy(bodies: Seq[RigidBody] = bodies, timeDelta: Double = timeDelta): World
}