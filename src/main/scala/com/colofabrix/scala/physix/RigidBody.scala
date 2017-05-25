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

import com.colofabrix.scala.EasyEquatable
import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math.Vect

/**
  * Represents a physical objects in the physical space with some physical details.
  */
trait RigidBody extends EasyEquatable[RigidBody] {

  /* Generic information */

  /** The mass of the rigid body */
  def mass: Double

  /** A geometric shape that defines the Body */
  def shape: Shape

  /* Linear physics values */

  /** Position of the center of mass of the object, relative to the origin of axes. */
  def position: Vect
  def lastPosition: Option[Vect]

  /** Speed of the object's center of mass, relative to the origin of the axes. */
  def velocity: Vect
  def lastVelocity: Option[Vect]

  /** The force that the object is generating internally. */
  def internalForce: Vect

  /* Rotational physics values */

  /*
  /** Rotation of the object's center of mass, relative to the origin of the axes. */
  def angle: Double

  /** Angular speed of the object's center of mass, relative to the origin of the axes. */
  def angularSpeed: Double

  /** The torque that the object is generating internally. */
  def torque: Double
  */

  /* Body/body Interaction values */

  /** A number (0.0, 1.0) that represent the amount of friction at the surface of the object. */
  def friction: Double

  /** A number (0.0, 1.0) that represent the elasticity at the surface of the object. */
  def elasticity: Double

  /* Actions of the body */

  /** Physically moves the body. */
  def move(position: Vect, velocity: Vect): RigidBody

  /* Utilities */

  /** Summary of the state of the object */
  def summary = s"Pos=$position, Vel=$velocity"

  /** A unique identifier for the object */
  val id: String = {
    val className = getClass
      .toString
      .replace("class ", "")
      .replaceFirst("(\\w+\\.)*", "")

    val uuid = java.util.UUID.randomUUID()
      .toString
      .substring(0, 8)

    s"$className@$uuid"
  }

  override def toString: String = this.id

  override def idFields: Seq[Any] = Seq(id)
}
