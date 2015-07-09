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
import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.gfx.OpenGL.Colour
import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import com.colofabrix.scala.gfx.renderers.CircleRenderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
 * Represents a bullet shot by a Tank
 *
 * Bullets are {PhysicalObject}s but they don't actively interact with other object, instead they are the subject
 * of Tank's interactions. This means that they don't implement {InteractiveObject}.
 */
class Bullet( override val world: World, val tank: Tank, val properSpeed: Double )
  extends PhysicalObject with Renderable {

  import Math._

  private var _life = 0

  /**
   * Resets the status of the `PhysicalObject
   * `
   * @return A new `PhysicalObject` where its internal status has been reset
   */
  override def clear( ): Unit = {}

  /**
   * Life of the bullet
   *
   * @return The number of ticks the bullet has been alive for
   */
  def life = _life

  /**
   * Position of the center of the PhysicalObject
   *
   * @return The point on the world where is the center of the PhysicalObject
   */
  _position = tank.position

  /**
   * Speed of the object relative to the arena
   *
   * Bullets have a proper speed summed to the one of the Tank and always less than the maximum allowed speed
   *
   * @return The current speed of a bullet
   */
  _speed = Vector2D.new_rt( properSpeed, tank.rotation.t ) + tank.speed
  _speed = _speed := { x => max( min( x, world.max_bullet_speed ), -world.max_bullet_speed ) }

  /**
   * Callback function used to signal the {PhysicalObject} that it has hit a wall (or it has gone beyond it)
   *
   * If a bullet hits a wall nothing is done, as we want the {World} to remove it, as if it simply flew away.
   */
  override def on_hitsWalls( ): Unit = {}

  /**
   * Callback function used to signal the {PhysicalObject} that is revolving faster than the maximum allowed angular speed
   *
   * Bullets don't rotate, their angle is fixed, so the implementation is empty
   */
  override def on_maxAngularSpeedReached( maxAngularSpeed: Double ): Unit = {}

  /**
   * Callback function used to signal the {PhysicalObject} that is moving faster than the maximum allowed speed
   *
   * Bullets speed is fixed when they are fired, there's no way they can exceed the maximum speed. If, by error the do, it's
   * of to let the world remove them. Empty implementation
   */
  override def on_maxSpeedReached( maxSpeed: Double ): Unit = {}

  /**
   * Callback function used to signal the {PhysicalObject} that it will be respawned in the next step
   *
   * Bullets are never respawned, so the implementation is empty
   */
  override def on_respawn( ): Unit = {}

  def renderer: Renderer = new CircleRenderer( objectShape.asInstanceOf[Circle], Colour.BLUE, true )

  /**
   * Physical boundary of the bullet.
   */
  override def objectShape: Shape = Circle( _position, 2 )

  /**
   * Moves the bullet one step into the future.
   */
  override def step( ) {
    _life += 1
    _position = _position + _speed
  }

  override def toString = id
}