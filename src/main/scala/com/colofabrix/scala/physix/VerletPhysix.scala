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

package com.colofabrix.scala.physix

import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.math.VectUtils._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig }

/**
  * Störmer–Verlet integration, position version
  *
  * External references:
  *  - https://en.wikipedia.org/wiki/Verlet_integration
  *  - https://www.gamedev.net/resources/_/technical/math-and-physics/a-verlet-based-approach-for-2d-game-physics-r2714
  *  - http://stackoverflow.com/tags/verlet-integration/info
  *  - http://lonesock.net/article/verlet.html
  *  - http://gafferongames.com/game-physics/integration-basics/
  */
trait VerletPhysix {
  self: RigidBody =>

  /** Position of the object at the last time step */
  protected def lastPosition: Vect

  /** Updates the status of the object */
  protected def update(p: Vect, pLast: Vect, v: Vect, a: Double, as: Double): RigidBody

  override def update(extForces: Vect, obstacles: Seq[RigidBody], bodies: Seq[RigidBody]): RigidBody = {
    // Total forces acting on the object (internal + external)
    val forces = this.internalForce + extForces
    // Acceleration
    val acc = forces.map(_ / this.mass)
    // Position
    val pos = 2.0 * this.position - lastPosition + acc * Math.pow(SimConfig.timeStep, 2.0)
    // Velocity, calculated as half step behind the current
    val vel = (pos - this.position) / SimConfig.timeStep

    return this.update(pos, this.position, vel, 0.0, 0.0)
  }
}

object VerletPhysix {
  def apply(body: RigidBody, extForces: Vect = Vect.zero): PosVerletEngine = {
    // Position Verlet needs the last 2 positions to calculate the next one
    val forces = body.internalForce + extForces
    val acc = forces.map(_ / body.mass)

    // Position at the step before initialization
    val lastPosition = body.position -
      body.velocity * SimConfig.timeStep -
      0.5 * acc * Math.pow(SimConfig.timeStep, 2.0)

    return new VerletPhysix(body, lastPosition)
  }
}
