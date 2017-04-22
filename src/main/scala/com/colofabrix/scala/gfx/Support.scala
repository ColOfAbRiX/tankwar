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

package com.colofabrix.scala.gfx

import com.colofabrix.scala.math.Vect

/** A colour in the OpenGL system */
final case class Colour(r: Double = 0.0, g: Double = 0.0, b: Double = 0.0)

/** Predefined colour set */
object Colour {
  val BLACK = Colour(0.0, 0.0, 0.0)
  val BLUE = Colour(0.0, 0.0, 1.0)
  val CYAN = Colour(0.0, 1.0, 1.0)
  val DARK_BLUE = Colour(0.1, 0.1, 0.3)
  val DARK_CYAN = Colour(0.0, 0.3, 0.3)
  val DARK_GREEN = Colour(0.1, 0.2, 0.1)
  val DARK_GREY = Colour(0.2, 0.2, 0.2)
  val DARK_MAGENTA = Colour(0.3, 0.0, 0.3)
  val DARK_RED = Colour(0.3, 0.1, 0.1)
  val DARK_YELLOW = Colour(0.3, 0.3, 0.0)
  val GREEN = Colour(0.0, 1.0, 0.0)
  val LIGHT_BLUE = Colour(0.8, 0.8, 1.0)
  val LIGHT_CYAN = Colour(8.0, 1.0, 1.0)
  val LIGHT_GREEN = Colour(0.8, 1.0, 0.8)
  val LIGHT_GREY = Colour(0.8, 0.8, 0.8)
  val LIGHT_MAGENTA = Colour(1.0, 0.8, 1.0)
  val LIGHT_RED = Colour(1.0, 0.8, 0.8)
  val LIGHT_YELLOW = Colour(1.0, 1.0, 0.8)
  val MAGENTA = Colour(1.0, 0.0, 1.0)
  val RED = Colour(1.0, 0.0, 0.0)
  val WHITE = Colour(1.0, 1.0, 1.0)
  val YELLOW = Colour(1.0, 1.0, 0.0)
}

/**
  * An OpenGL reference frame. It holds data about its position to the absolute reference, its rotation and the
  * colour of its brush
  */
final case class Frame(
  colour: Option[Colour],
  position: Option[Vect],
  rotation: Option[Vect]
)

object Frame {
  def apply(
    colour: Colour = null,
    position: Vect = null,
    rotation: Vect = null
  ): Frame = Frame(
    Option(colour),
    Option(position),
    Option(rotation)
  )
}