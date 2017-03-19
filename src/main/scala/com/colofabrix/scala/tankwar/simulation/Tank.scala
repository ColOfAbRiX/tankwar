/*
 * Copyright (C) 2017 Fabrizio
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

package com.colofabrix.scala.tankwar.simulation

import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.physix.RigidBody
import com.typesafe.scalalogging.LazyLogging

/**
  * A Tank that plays in the game
  */
class Tank private(
  val mass: Double,
  val position: Vect,
  val velocity: Vect,
  val angle: Double,
  val angularSpeed: Double,
  val friction: Double,
  val bounciness: Double,
  _id: Option[String] = None,
  _force: Option[Vect] = None,
  _torque: Option[Double] = None
) extends RigidBody with LazyLogging {

  /** A unique identifier for the object */
  override val id: String = _id.getOrElse(java.util.UUID.randomUUID().toString)

  /** The force that the object is generating */
  override def internalForce: Vect = _force.getOrElse(Vect.zero)

  /** Angular speed of the object's main axis */
  override def torque: Double = _torque.getOrElse(0.0)

  /** Updates the status of the object */
  override def update(position: Vect, velocity: Vect, angle: Double, angularSpeed: Double): Tank = {
    logger.info(s"Updating with new info: Pos: $position, Vel: $velocity")

    return new Tank(
      mass, position, velocity, angle, angularSpeed,
      friction, bounciness,
      Some(id), Some(internalForce), Some(torque)
    )
  }
}

object Tank {
  def apply(
    mass: Double = 1.0,
    position: Vect = Vect.zero,
    velocity: Vect = Vect.zero,
    angle: Double = 0.0,
    angularSpeed: Double = 0.0,
    friction: Double = 0.0,
    bounciness: Double = 1.0
  ): Tank = {
    return new Tank(mass, position, velocity, angle, angularSpeed, friction, bounciness)
  }
}