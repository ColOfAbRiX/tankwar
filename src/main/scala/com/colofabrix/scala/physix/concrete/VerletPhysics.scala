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

import com.colofabrix.scala.math.Vect
import com.colofabrix.scala.math.VectUtils._
import com.colofabrix.scala.physix.{ PhysixEngine, RigidBody }
import com.typesafe.scalalogging.LazyLogging

/**
  * Game physics, implemented using the Velocity Verlet integrator
  */
final case
class VerletPhysics() extends PhysixEngine with LazyLogging {

  /** Calculate the initial values of last position and velocity. */
  override
  def init(mass: Double, position: Vect, velocity: Vect) = scalaz.State { ctx =>
    val acc = ctx.forceField(position) / mass
    val result = Tuple2(
      velocity - acc * ctx.timeDelta,
      position - velocity * ctx.timeDelta - 0.5 * acc * ctx.timeDelta * ctx.timeDelta
    )
    (ctx, result)
  }

  /** Move one body one time step into the future. */
  override
  def moveBody(body: RigidBody) = scalaz.State { ctx =>
    val totalForce = body.internalForce + ctx.forceField(body.position)
    val actualForce = totalForce * (1.0 - ctx.friction(body)(body.position))

    // Using Newton's laws to calculate next step state
    val acceleration = actualForce / body.mass
    val velocity = body.velocity + acceleration * ctx.timeDelta
    // Using velocity Verlet integration
    val position = body.position + 0.5 * (body.lastVelocity + velocity) * ctx.timeDelta

    // Return an updated body
    val result = body.move(position, velocity)
    (ctx, result)
  }

  /** Move the whole physics one time step into the future. */
  override
  def step() = scalaz.State { ctx =>
    for {
      b <- ctx.bodies
    } yield _

    val result = for {b <- ctx.bodies} yield {
      moveBody(b).run(ctx)._2
    }
    (ctx, result)
  }
}
