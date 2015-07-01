/*
 * Copyright (C) 2015 Freddie Poser
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
 * Created by Freddie on 19/05/2015.
 *
 * Class to hold a 3-Double colour nicely
 *
 */
class Color3d (val r:Double = 0, val g:Double = 0, val b:Double = 0) {


  /**
   * Use this to bind opengl to the color (glColor3d(r,g,b))
   */
  def bind (): Unit = {
    GL11.glColor3d(r,g,b)
  }

  /**
   * @return This colour as a Slick2D color
   */
  def toSlickColor  : Color = new Color(r.toFloat, g.toFloat, b.toFloat)

}


/**
 * Holds constants that represent common colours
 */
object Color3d {
  val RED = new Color3d(1,0,0)
  val GREEN = new Color3d(0,1,0)
  val BLUE = new Color3d(0,0,1)
  val BLACK = new Color3d(0,0,0)
  val LIGHT_GREY = new Color3d(0.8,0.8,0.8)
  val DARK_GREY = new Color3d(0.2,0.2,0.2)
  val WHITE = new Color3d(1,1,1)

}
