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

package com.colofabrix.scala.gfx.renderers

import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.Vector2D
import org.lwjgl.opengl.GL11._


/**
 * Draws an applied vector on screen
 *
 * An applied vector is a vector with a tail that is not in the origin of axes
 *
 * @param vector The vector to draw
 * @param apply Its apply point (where its tail begins)
 * @param size The maximum size of the arrow
 * @param defaultFrame The default frame to use when a new drawing context has to be created
 */
class VectorRenderer(
  val vector: Vector2D,
  val apply: Vector2D = Vector2D.zero,
  size: Double = 5,
  defaultFrame: Frame = Frame( Colour.WHITE )
) extends Renderer {

  /**
   * Draws the vector
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  def render( create: Boolean = true ): Unit = {

    withDefaultContext( create, defaultFrame ) {

      // This sets the position to the application point of the vector
      applyContext( Frame( _position = apply ) ) {

        // Draw the main line
        drawOpenGL( GL_LINES ) {
          glVertex2d( 0, 0 )
          glVertex2d( vector.x, vector.y )
        }

        // Draw the arrow of the vector
        val arrowSize = Math.min( size, vector.r / 4 )
        drawOpenGL( GL_TRIANGLES, Frame( _position = vector, _rotation = vector ) ) {
          glVertex2d( arrowSize, 0.0 )
          glVertex2d( -0.866025 * arrowSize, 0.5 * arrowSize )
          glVertex2d( -0.866025 * arrowSize, -0.5 * arrowSize )
        }

      }

    }

  }

}