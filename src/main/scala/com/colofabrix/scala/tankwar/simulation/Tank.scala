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

package com.colofabrix.scala.tankwar.simulation

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.physix.{ RigidBody, VerletPhysix }
import com.colofabrix.scala.tankwar.Configuration.{ Tanks => TanksConfig }
import com.typesafe.scalalogging.LazyLogging

/**
  * A Tank that plays in the game
  */
final
class Tank(

  initialPosition: Vect = Vect.zero,
  initialVelocity: Vect = Vect.zero,
  initialAngle: Double = 0.0,
  initialAngularSpeed: Double = 0.0,
  initialExternalForce: Vect

) extends VerletPhysix(

  TanksConfig.defaultMass,
  initialPosition,
  initialVelocity,
  initialAngle,
  initialAngularSpeed,
  initialExternalForce

) with LazyLogging {

  logger.info(s"Initialzed($id): " + summary)

  override
  def step(walls: Seq[Shape], bodies: Seq[RigidBody], extForces: Vect): Tank = {
    val newTank = super.step(walls, bodies, extForces).asInstanceOf[Tank]

    logger.info(s"Step($id): " + newTank.summary)

    return newTank
  }

  override
  def internalForce = Vect.zero

  override
  def torque = 0.0

  override
  def shape = Circle(this.position, 10.0)

  override
  val friction = 0.0

  override
  val elasticity = 1.0
}