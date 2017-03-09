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

package com.colofabrix.scala.tankwar.physics

import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.math.VectUtils._
import com.colofabrix.scala.tankwar.geometry.Shape
import PhysicsUtils._


/**
  *
  */
object PhysixEngineVerlet {

  import com.colofabrix.scala.tankwar.Configuration.{ Simulation ⇒ SimConfig }

  /**
    * Extended simulation information for the objects in the world
    *
    * @param physicalObject Reference to the object
    * @param lastPosition   The position of the previous step, used by the Verlet integrator
    */
  class PhysixInfo private(val physicalObject: PhysxObject, private val lastPosition: Vect) {

    /**
      * Störmer–Verlet integration
      *
      * External references:
      *  - https://en.wikipedia.org/wiki/Verlet_integration
      *  - https://www.gamedev.net/resources/_/technical/math-and-physics/a-verlet-based-approach-for-2d-game-physics
      * -r2714
      *  - http://lonesock.net/article/verlet.html
      *  - http://gafferongames.com/game-physics/integration-basics/
      */
    def motion(extForces: Vect = Vect.zero): PhysixInfo = {
      val totalForces = physicalObject.internalForce + extForces
      val acceleration = totalForces.map(_ / physicalObject.mass)
      val position = 2.0 * physicalObject.position - lastPosition + acceleration * Math.pow(SimConfig.timeStep, 2.0)
      val velocity = (position - physicalObject.position) / SimConfig.timeStep

      return new PhysixInfo(
        physicalObject.update(position, velocity, 0.0, 0.0),
        physicalObject.position
      )
    }

    def borders(constraints: Seq[Shape]): PhysixInfo = {
      return this
    }

    def collision(colliding: Seq[PhysixInfo]): PhysixInfo = {
      return this
    }
  }

  object PhysixInfo {
    def apply(obj: PhysxObject, extForces: Vect = Vect.zero): PhysixInfo = {
      return new PhysixInfo(obj, obj.position)
    }
  }

}
