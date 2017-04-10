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

import scalaz.{ -\/, \/- }
import com.colofabrix.scala.geometry.{ Collision, Shape }
import com.colofabrix.scala.math._
import com.colofabrix.scala.tankwar.Configuration.{ Simulation ⇒ SimConfig }
import com.typesafe.scalalogging.LazyLogging

/**
  * Verlet integration, position version
  */
abstract class VerletPhysix(

    override val mass: Double,
    private var _position: Vect,
    private var _velocity: Vect,
    private var _angle: Double,
    private var _angularSpeed: Double,
    initialExternalForce: Vect

) extends RigidBody with LazyLogging {

  import com.colofabrix.scala.math.VectUtils._

  /** Position of the object at the last step. */
  final protected def lastVelocity = _lastVelocity

  private var _lastVelocity: Vect = Vect.zero

  override def step(walls: Seq[Shape], bodies: Seq[RigidBody], extForces: Vect = Vect.zero): VerletPhysix = {
    this._lastVelocity = this.velocity

    val acc = (this.internalForce + extForces) comp (_ / this.mass)
    this._velocity += acc * SimConfig.timeStep

    val checkShape = this.shape.moveOf(
      0.5 * (this.lastVelocity + this.velocity) * SimConfig.timeStep
    )

    for (w ← walls) {

      w.collision(checkShape) match {
        case -\/(Collision(n, d)) ⇒
          val v = this.velocity ∙ n
          if ((d ~< 0.0) && (v ~< 0.0)) {
            this._velocity -= 2.0 * v * n
            logger.info(s"Collision detected with $w and Tank position $checkShape. New velocity: $velocity")
          }

        case \/-(Collision(n, d)) ⇒
      }

    }

    this._position += 0.5 * (this.lastVelocity + this.velocity) * SimConfig.timeStep
    return this
  }

  final override def position: Vect = _position

  final override def velocity: Vect = _velocity

  final override def angle: Double = _angle

  final override def angularSpeed: Double = _angularSpeed
}
