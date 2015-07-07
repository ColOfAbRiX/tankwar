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

import com.colofabrix.scala.math.Vector2D
import org.lwjgl.opengl.GL11._

/**
 * Created by Fabrizio Colonna on 05/07/2015.
 */
object OpenGL {

  /**
   * Sets the color of the brush
   *
   * @param color The color to set
   */
  def bindColor( color: Color3D ) {
    if( color != null ) {
      color.bind( )
    }
  }

  /**
   * Initialize the environment to draw something on screen
   *
   * @param mode What to draw on screen, see OpenGL for more details
   * @param position The position of the frame of reference in the draw
   * @param rotation The rotation of the frame of reference in the draw
   * @param drawFunction The function that actually draw
   */
  def draw( mode: Int, position: Vector2D = Vector2D.zero, rotation: Vector2D = Vector2D.zero )( drawFunction: => Unit ) {
    // Move the reference
    glTranslated( position.x, position.y, 0 )
    // And rotates the reference
    if( rotation.t != 0 )
      glRotated( rotation.t * 180 / Math.PI, 0, 0, 1 )

    // Initialize the draw
    glBegin( mode )

    // Execute whatever the user wants
    drawFunction

    // Terminate
    glEnd( )
  }

  def drawContext( contextActions: => Unit ): Unit = {
    // Initialize the drawing context
    glPushMatrix( )
    glTranslated( 0, 0, 0 )
    glRotated( 0, 0, 0, 0 )

    // Execute what the user wants
    contextActions

    // Terminate the drawing context
    glPopMatrix( )
  }

}
