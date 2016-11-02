/*
 * Copyright (C) 2016 Fabrizio
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

import com.colofabrix.scala.math.Vect
import scala.pickling.Defaults._
import scala.pickling.json._

/**
  * Represents a physical objects in the physical space with some physical details.
  *
  * A PhysicalObject can easily fall in the category of stateful objects. For this reason some members are provided as
  * internally read-write but read-only for clients.
  */
@SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
trait PhysicalObject {

  /**
    * Position of the center of the object
    *
    * All spatial information of the object must be related to this one.
    *
    * @return The point on the world where is the center of the {PhysicalObject}
    */
  def position: Vect

  /**
    * Speed of the {position} of the object, relative to the arena.
    *
    * Note that the speed is the speed of the {position} and if the {PhysicalObject} is doing other movements this
    * field will not tell anything about the other movements.
    * The speed is relative to a single step of the world or, in other words, speed is given in points/step
    *
    * @return The current speed of the object
    */
  def speed: Vect

  /**
    * Rotation of the object relative to the X-axis
    *
    * The main axis of a {PhysicalObject} is the axis that passes through the {position} point. At zero radians or rotation
    * the axis is parallel to the X-axis.
    *
    * @return A versor indicating the angle formed by the object's main axis and the X-axis, in radians
    */
  def rotation: Double

  /**
    * Angular speed of the object's main axis
    *
    * The angular speed is relative to a single step of the world or, in other words, speed is given in radians/step
    *
    * @return The current angular speed
    */
  def angularSpeed: Double

  /**
    * Mass of the PhysicalObject
    *
    * @return The mass of the object
    */
  def mass: Double

  /**
    * Identifier of the instance.
    *
    * The default implementation is to return the class name with an hash appended to it, like Object@14235
    */
  final val id = this.getClass.toString.replace( "class ", "" ).replaceFirst( """(\w+\.)*""", "" ) + "@" + java.util.UUID.randomUUID.toString.substring( 0, 5 )

  /**
    * Record identifying the step of the PhysicalObject
    *
    * The string returns information about the status of the objects in a CSV format that can be used to. Derived classes
    * can extend this definition adding or changing it for their purposes
    *
    * @return A string in the format of a CSV
    */
  def record = s"$id,${position.x},${position.y},${speed.x},${speed.y}"

  def serialize = this.pickle
}
