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

package com.colofabrix.scala.gfx

import org.lwjgl.opengl.GL11
import org.newdawn.slick.Color

/**
 * Class to hold a 3-Double colour nicely
 */
class Color3D( val r: Double = 0, val g: Double = 0, val b: Double = 0 ) {

  /**
   * Bind a colour to the OpenGL context
   */
  def bind( ): Unit = GL11.glColor3d( r, g, b )

  /**
   * This colour as a Slick2D color
   *
   * @return This colour as a Slick2D color
   */
  def toSlickColor: Color = new Color( r.toFloat, g.toFloat, b.toFloat )

}

object Color3D {
  val RED = new Color3D( 1, 0, 0 )
  val GREEN = new Color3D( 0, 1, 0 )
  val BLUE = new Color3D( 0, 0, 1 )

  val LIGHT_GREY = new Color3D( 0.8, 0.8, 0.8 )
  val LIGHT_RED = new Color3D( 1.0, 0.8, 0.8 )
  val LIGHT_GREEN = new Color3D( 0.8, 1.0, 0.8 )
  val LIGHT_BLUE = new Color3D( 0.8, 0.8, 1.0 )

  val DARK_GREY = new Color3D( 0.2, 0.2, 0.2 )
  val DARK_RED = new Color3D( 0.3, 0.1, 0.1 )
  val DARK_GREEN = new Color3D( 0.1, 0.2, 0.1 )
  val DARK_BLUE = new Color3D( 0.1, 0.1, 0.3 )

  val BLACK = new Color3D( 0, 0, 0 )
  val WHITE = new Color3D( 1, 1, 1 )
}
