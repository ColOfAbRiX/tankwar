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

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math.Vect

/**
  * Represents a physical objects in the physical space with some physical details.
  */
trait RigidBody {

  /* Generic information */

  /** The mass of the rigid body */
  def mass: Double

  /** A geometric shape that defines the Body */
  def shape: Shape

  /** A unique identifier for the object */
  val id: String = {
    val className = getClass
      .toString
      .replace("class ", "")
      .replaceFirst("(\\w+\\.)*", "")

    val uuid = java.util.UUID.randomUUID()
      .toString
      .substring(0, 6)

    s"$className@$uuid"
  }

  /** Identifier of the instance. */
  override def toString: String = this.id

  /** Summary of the state of the object */
  def summary = s"Pos=$position, Vel=$velocity, Ang=$angle, AngSped=$angularSpeed"

  /* Linear physics values */

  /** Position of the center of mass of the object, relative to the origin of axes. */
  def position: Vect

  /** Speed of the object's center of mass, relative to the origin of the axes. */
  def velocity: Vect

  /** The force that the object is generating internally. */
  def internalForce: Vect

  /* Rotational physics values */

  /** Rotation of the object's center of mass, relative to the origin of the axes. */
  def angle: Double

  /** Angular speed of the object's center of mass, relative to the origin of the axes. */
  def angularSpeed: Double

  /** The torque that the object is generating internally. */
  def torque: Double

  /* Body/body Interaction values */

  /** A number (0.0, 1.0) that represent the amount of friction at the surface of the object. */
  def friction: Double

  /** A number (0.0, 1.0) that represent the elasticity at the surface of the object. */
  def elasticity: Double

  /* Actions */

  /** Calculates the RigidBody for the next step */
  def step(walls: Seq[Shape], bodies: Seq[RigidBody], extForces: Vect = Vect.zero): RigidBody
}
