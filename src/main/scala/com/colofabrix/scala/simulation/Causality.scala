/*
 * Copyright (C) 2015 Fabrizio Colonna
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

package com.colofabrix.scala.simulation

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

import scala.reflect.ClassTag

/**
 * Object that allow casuality
 *
 * In the simulation only a direct collision is allowed and it's not possible to simulate something like "a tank
 * that hits another tank by an intermediate object (a bullet)" because this resultant collision is mediated by bullets.
 * This objects is, by all means, a `PhysicalObject` but it encapsulate the original object that gave start to the
 * sequence of events that ultimately led to the collision.
 *
 * @param initial The initial object that gave start to the this collision
 * @param t A type used to investigate the parameter `initial`
 * @tparam T The type of object for `T`
 */
class Causality[+T <: PhysicalObject]( val initial: T )( implicit t: ClassTag[T] ) extends PhysicalObject {
  /**
   * Resets the status of the `PhysicalObject
   * `
   * @return A new `PhysicalObject` where its internal status has been reset
   */
  override def clear( ): Unit = initial.clear( )

  /**
   * Physical shape of the {PhysicalObject}.
   *
   * It is used to interact with the {World} itself, to display the {PhysicalObject} or to interact with other
   * {com.colofabrix.scala.geometry.abstracts.InteractiveObject}
   */
  override def objectShape: Shape = initial.objectShape

  /**
   * Callback function used to signal the {PhysicalObject} that it has hit a wall (or it has gone beyond it)
   */
  override def on_hitsWalls( ): Unit = initial.on_hitsWalls( )

  /**
   * Callback function used to signal the {PhysicalObject} that is revolving faster than the maximum allowed angular speed
   */
  override def on_maxAngularSpeedReached( maxAngularSpeed: Double ): Unit = on_maxAngularSpeedReached( maxAngularSpeed )

  /**
   * Callback function used to signal the {PhysicalObject} that is moving faster than the maximum allowed speed
   */
  override def on_maxSpeedReached( maxSpeed: Double ): Unit = on_maxSpeedReached( maxSpeed )

  /**
   * Callback function used to signal the {PhysicalObject} that it will be respawned in the next step
   */
  override def on_respawn( ): Unit = initial.on_respawn( )

  /**
   * Moves the PhysicalObject one step into the future.
   *
   * In other words, this method will perform the needed calculations to updated all the internal field of the objects
   * like the position in the next step, speed and so on.
   */
  override def step( ): Unit = initial.step( )

  /**
   * Reference to the world
   *
   * The world can be used to obtain information about the simulation, the other objects or the limits of the world
   */
  override def world: World = initial.world
}