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

import scalaz._
import com.colofabrix.scala.geometry.{ Collision, Shape }
import com.colofabrix.scala.math.VectUtils._
import com.colofabrix.scala.math._
import com.colofabrix.scala.physix.{ PhysixEngine, RigidBody, World }
import com.typesafe.scalalogging.LazyLogging

/**
  * Game physics, implemented using the Velocity Verlet integrator
  */
final case class VerletPhysics() extends PhysixEngine with LazyLogging {
  logger.trace("Initializing Velocity-Verlet PhysixEngine.")

  private case class InternalState(
    original: World,
    world: World
  )

  override def step() = State { world: World =>
    val actions = for {
      _ <- moveBodies()
      _ <- wallsCollision()
      r <- bodiesCollision()
    } yield r

    val result = actions.run(InternalState(world, world))
    (result._1.world, result._2)
  }

  private def moveBodies() = State { ctx: InternalState =>
    val newBodies = for {
      b <- ctx.world.bodies
    } yield {
      moveBody(b, ctx.world)
    }

    (ctx.copy(world = ctx.world.copy(bodies = newBodies)), newBodies)
  }

  private def moveBody(body: RigidBody, world: World): RigidBody = {
    // The velocity Verlet requires the velocity at the last step which we don't
    // have at the first iteration
    val lastVelocity = body.lastVelocity match {
      case Some(lv) => lv
      case _ =>
        val acc = world.forceField(body.position) / body.mass
        body.velocity - acc * world.timeDelta
    }

    val totalForce = body.internalForce + world.forceField(body.position)
    val actualForce = totalForce * (1.0 - world.friction(body)(body.position))

    // Using Newton's laws to calculate next step state
    val acceleration = actualForce / body.mass
    val velocity = body.velocity + acceleration * world.timeDelta
    // Using velocity Verlet integration
    val position = body.position + 0.5 * (lastVelocity + velocity) * world.timeDelta

    // Return an updated body
    body.move(position, velocity)
  }

  private def wallsCollision() = State { ctx: InternalState =>
    val newBodies = for {
      b <- ctx.world.bodies
    } yield {
      ctx.world.walls.foldLeft(b)(wallCollision(_, _))
    }

    (ctx.copy(world = ctx.world.copy(bodies = newBodies)), newBodies)
  }

  private def wallCollision(b: RigidBody, w: Shape): RigidBody = {
    w collision b.shape match {
      case -\/(Collision(n, d)) =>
        val v = b.velocity ∙ n
        val r = if (d <~ 0.0 && v <~ 0.0) -2.0 * v * n else Vect.zero
        b.move(velocity = b.velocity + r)

      case _ => b
    }
  }

  private def bodiesCollision() = State { ctx: InternalState =>
    (ctx, ctx.world.bodies)
  }
}
