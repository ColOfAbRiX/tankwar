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

package com.colofabrix.scala.simulation.abstracts

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.math.{ RTVect, Vect, XYVect }
import com.colofabrix.scala.simulation.World

/**
  * Represents a physical objects in the physical space with some physical details.
  *
  * A {PhysicalObject} can easily fall in the category of stateful objects. For this reason some members are provided as
  * internally read-write but read-only for clients.
  */
@SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
trait PhysicalObject {
  /**
    * Reference to the world
    *
    * The world can be used to obtain information about the simulation, the other objects or the limits of the world
    */
  def world: World

  /**
    * Physical shape of the {PhysicalObject}.
    *
    * It is used to interact with the {World} itself, to display the {PhysicalObject} or to interact with other
    * {com.colofabrix.scala.geometry.abstracts.InteractiveObject}
    */
  def objectShape: Shape

  /**
    * Resets the status of the `PhysicalObject
    * `
    *
    * @return A new `PhysicalObject` where its internal status has been reset
    */
  def clear(): Unit

  /**
    * Position of the center of the object
    *
    * All spatial information of the object must be related to this one.
    *
    * @return The point on the world where is the center of the {PhysicalObject}
    */
  final def position: Vect = _position

  protected var _position: Vect = XYVect( 0, 0 )

  /**
    * Speed of the {position} of the object, relative to the arena.
    *
    * Note that the speed is the speed of the {position} and if the {PhysicalObject} is doing other movements this
    * field will not tell anything about the other movements.
    * The speed is relative to a single step of the world or, in other words, speed is given in points/step
    *
    * @return The current speed of the object
    */
  final def speed: Vect = _speed

  protected var _speed: Vect = XYVect( 0, 0 )

  /**
    * Rotation of the object relative to the X-axis
    *
    * The main axis of a {PhysicalObject} is the axis that passes through the {position} point. At zero radians or rotation
    * the axis is parallel to the X-axis.
    *
    * @return A versor indicating the angle formed by the object's main axis and the X-axis, in radians
    */
  final def rotation: Vect = _rotation

  protected var _rotation: Vect = RTVect( 1, 0 )

  /**
    * Angular speed of the object's main axis
    *
    * The angular speed is relative to a single step of the world or, in other words, speed is given in radians/step
    *
    * @return The current angular speed
    */
  final def angularSpeed: Double = _angularSpeed

  protected var _angularSpeed: Double = 0.0

  /**
    * Mass of the PhysicalObject
    *
    * @return The mass of the object
    */
  final def mass: Double = _mass

  protected var _mass: Double = 1.0

  /**
    * Moves the PhysicalObject one step into the future.
    *
    * In other words, this method will perform the needed calculations to updated all the internal field of the objects
    * like the position in the next step, speed and so on.
    */
  def step(): Unit

  /**
    * Determines if a point lies inside the Shape
    *
    * This is a shortcut to {objectShape.intersects} with a {Vect} as argument
    *
    * @param that The point to check
    * @return true if the point is inside or on the boundary of the shape
    */
  def intersects( that: Vect ): Boolean = objectShape.contains( that )

  /**
    * Determines if a shape touches this one
    *
    * This is a shortcut to {objectShape.intersects} with a {Shape} as argument
    *
    * @param that The shape to check
    * @return true if the two shapes overlap
    */
  def touches( that: PhysicalObject ): Boolean = objectShape.intersects( that.objectShape )

  /**
    * Identifier of the instance.
    *
    * The default implementation is to return the class name with an hash appended to it, like Object@14235
    */
  val id = this.getClass.toString.replace( "class ", "" ).replaceFirst( """(\w+\.)*""", "" ) + "@" + java.util.UUID.randomUUID.toString.substring( 0, 5 )

  /**
    * Record identifying the step of the PhysicalObject
    *
    * The string returns information about the status of the objects in a CSV format that can be used to. Derived classes
    * can extend this definition adding or changing it for their purposes
    *
    * @return A string in the format of a CSV
    */
  def record = s"$id,${world.time},${position.x},${position.y},${speed.x},${speed.y}"

  /**
    * Callback function used to signal the {PhysicalObject} that it has hit a wall (or it has gone beyond it)
    */
  def on_hitsWalls(): Unit

  /**
    * Callback function used to signal the {PhysicalObject} that is moving faster than the maximum allowed speed
    */
  def on_maxSpeedReached( maxSpeed: Double ): Unit

  /**
    * Callback function used to signal the {PhysicalObject} that is revolving faster than the maximum allowed angular speed
    */
  def on_maxAngularSpeedReached( maxAngularSpeed: Double ): Unit

  /**
    * Callback function used to signal the {PhysicalObject} that it will be respawned in the next step
    */
  def on_respawn(): Unit
}