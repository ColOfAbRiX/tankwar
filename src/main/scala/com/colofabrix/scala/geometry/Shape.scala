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

package com.colofabrix.scala.geometry

import com.colofabrix.scala.math.Vect

/**
  * Represents a geometric closed shape on a geometric space
  *
  * This train contains definitions of basic geometric operations like distance from
  * a point or a check to see if a line segment intersects the @see Shape. Upon this
  * elementary operation the derived shapes will build their behaviour and specialize
  * the implementation to take advantage of their properties for a faster access
  */
trait Shape {

  /** The surface area of the Shape */
  def area: Double

  /** Check if two shapes collide. */
  def collide(s: Shape): Boolean = Collision.collide(this, s)

  /** Returns collision information if two Shapes collide */
  def collisionInfo(s: Shape, pos1: Vect, pos2: Vect): Collision = Collision.info(this, s, pos1, pos2)

  /** Moves a shape of the given vector. */
  def move(v: Vect): Shape
}


