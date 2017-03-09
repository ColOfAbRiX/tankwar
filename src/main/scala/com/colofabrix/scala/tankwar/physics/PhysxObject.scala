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

import scala.pickling.Defaults._
import scala.pickling.json._

/**
  * Represents a physical objects in the physical space with some physical details.
  *
  * A PhysicalObject can easily fall in the category of stateful objects. For this reason some members are provided as
  * internally read-write but read-only for clients.
  */
trait PhysxObject {
  /** A unique identifier for the object */
  def id: String

  /** Mass of the Object. */
  def mass: Double

  /** Position of the center of mass of the object, relative to the origin of axes. */
  def position: Vect

  /** Speed of the object's center of mass, relative to the origin of the axes. */
  def velocity: Vect

  /** The force that the object is generating internally. */
  def internalForce: Vect

  /** Rotation of the object's center of mass, relative to the origin of the axes. */
  def angle: Double

  /** Angular speed of the object's center of mass, relative to the origin of the axes. */
  def angularSpeed: Double

  /** The torque that the object is generating internally. */
  def torque: Double

  /** Updates the status of the object */
  def update(p: Vect, v: Vect, a: Double, as: Double): PhysxObject

  /** Identifier of the instance. */
  override def toString: String = this.getClass.toString.replace( "class ", "" ).replaceFirst( """(\w+\.)*""", "" ) + "@" + this.id

  /** Record identifying the step of the PhysicalObject */
  def record = s"ID: $id, Pos: $position, Vel: $velocity"

  /** Serialization for the object */
  def serialize = this.pickle
}
