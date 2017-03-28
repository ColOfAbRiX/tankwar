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

import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.math.VectUtils._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig }

/**
  * Störmer–Verlet integration, position version
  *
  * External references:
  *  - https://en.wikipedia.org/wiki/Verlet_integration
  *  - http://lonesock.net/article/verlet.html
  *  - https://www.gamedev.net/resources/_/technical/math-and-physics/a-verlet-based-approach-for-2d-game-physics-r2714
  *  - http://stackoverflow.com/tags/verlet-integration/info
  */
abstract class VerletPhysix(
  override val mass: Double,
  private var _position: Vect,
  private var _velocity: Vect,
  private var _angle: Double,
  private var _angularSpeed: Double,
  initialExternalForce: Vect
) extends RigidBody {

  /** Position of the object at the last step. */
  final def lastPosition = _lastPosition

  private var _lastPosition: Vect = {
    // Position at the step before initialization
    val acc = (initialExternalForce + internalForce).comp(_ / mass)
    position - velocity * SimConfig.timeStep - 0.5 * acc * Math.pow(SimConfig.timeStep, 2.0)
  }

  override def step(extForces: Vect, walls: Seq[RigidBody], bodies: Seq[RigidBody]): VerletPhysix = {
    val lastPos = position

    val forces = this.internalForce + extForces
    val acc = forces.comp(_ / this.mass)
    _position = 2.0 * this.position - lastPosition + acc * Math.pow(SimConfig.timeStep, 2.0)
    _velocity = (position - lastPos) / SimConfig.timeStep

    _lastPosition = lastPos
    return this
  }

  final override def position: Vect = _position

  final override def velocity: Vect = _velocity

  final override def angle: Double = _angle

  final override def angularSpeed: Double = _angularSpeed
}
