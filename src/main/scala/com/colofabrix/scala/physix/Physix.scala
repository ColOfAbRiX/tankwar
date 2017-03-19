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

package com.colofabrix.scala.physix

import com.colofabrix.scala.geometry.Shape
import com.colofabrix.scala.math.Vect

/**
  * Extended simulation information for the objects in the world
  */
trait Physix {

  /**
    * The object referenced by the PhysixEngine
    */
  def physicalObject: RigidBody

  /**
    * Calculates the motion of the object given the external forces
    */
  def motion(extForces: Vect = Vect.zero): Physix

  /**
    * Calculates the interactions with borders given the stapes that compose it
    */
  def borders(constraints: Seq[Shape]): Physix

  /**
    * Calculates the effects of collision with other objects
    */
  def collision(colliding: Seq[RigidBody]): Physix
}
