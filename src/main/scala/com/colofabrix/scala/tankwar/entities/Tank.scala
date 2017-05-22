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

package com.colofabrix.scala.tankwar.entities

import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.physix.{ PhysixEngine, RigidBody, World }
import com.colofabrix.scala.tankwar.Configuration.{ Tanks => TanksConfig }
import com.typesafe.scalalogging.LazyLogging

/**
  * A Tank that plays in the game
  */
final case
class Tank private(
  mass: Double,
  position: Vect,
  lastPosition: Vect,
  velocity: Vect,
  lastVelocity: Vect,
  friction: Double,
  elasticity: Double
) extends RigidBody with LazyLogging {
  logger.info(s"Initialzed($id): $summary")

  override
  def internalForce = Vect.zero

  override
  def shape = Circle(this.position, 10.0)

  override
  def move(position: Vect = this.position, velocity: Vect = this.position): Tank = {
    new Tank(mass, position, this.position, velocity, this.velocity, friction, elasticity)
  }

  override
  def canEqual(a: Any): Boolean = a.isInstanceOf[Tank]
}

object Tank {
  def apply(
    world: World,
    timeDelta: Double,
    physix: PhysixEngine,
    position: Vect = Vect.zero,
    velocity: Vect = Vect.zero,
    mass: Double = TanksConfig.mass
  ) = {
    val (lastPosition, lastVelocity) = physix.init(world, timeDelta, mass, position, velocity)
    new Tank(mass, position, lastPosition, velocity, lastVelocity, TanksConfig.friction, TanksConfig.elasticity)
  }
}
