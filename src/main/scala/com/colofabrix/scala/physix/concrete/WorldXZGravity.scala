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

package com.colofabrix.scala.physix.concrete

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.geometry.shapes.Line
import com.colofabrix.scala.math._
import com.colofabrix.scala.physix.{ RigidBody, World }
import com.colofabrix.scala.tankwar.Configuration.{ World => WorldConfig }
import com.typesafe.scalalogging.LazyLogging

/**
  * World as a slice in the vertical plane of the real world, with gravity and static friction.
  */
final case class WorldXZGravity(
    bodies: Seq[RigidBody],
    timeDelta: Double
) extends World with LazyLogging {
  logger.trace(s"Initializing WorldXZGravity World.")
  logger.trace(s"List of bodies: $bodies")
  logger.trace(s"Time delta: $bodies")

  override def forceField: VectorField = _ => XYVect(0.0, -9.80665)

  override def friction(body: RigidBody): ScalarField = _ => body.mass * 1.0E-2 + Math.pow(body.velocity.ρ, 2.0) * 1.0E-3

  override def walls: Seq[Shape] = Seq(
    // Ceiling
    Line(XYVect(0.0, -1.0), WorldConfig.Arena.height),
    // Left side
    Line(XYVect(1.0, 0.0), 0.0),
    // Floor
    Line(XYVect(0.0, 1.0), 0.0),
    // Right side
    Line(XYVect(-1.0, 0.0), WorldConfig.Arena.width)
  )

  override def copy(
    bodies: Seq[RigidBody] = bodies,
    timeDelta: Double = timeDelta
  ): WorldXZGravity = WorldXZGravity(bodies, timeDelta)
}