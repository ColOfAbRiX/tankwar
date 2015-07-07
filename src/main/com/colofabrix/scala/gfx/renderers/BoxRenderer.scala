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

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import org.lwjgl.opengl.GL11._


/**
 * Renders a box to the screen
 *
 * Draw a box on the screen
 * @param box The box to draw on screen
 * @param colour The colour of the box
 * @param filled True indicated the circle has to be filled. It defaults to false
 */
class BoxRenderer( val box: Box, colour: Colour = null, filled: Boolean = false ) extends Renderer {

  /**
   * Render a box to the screen
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  def render( create: Boolean = true ): Unit = {

    withContext( create, Frame( colour, box.bottomLeft ) ) {

      val mode = if( filled ) GL_QUADS else GL_LINE_LOOP
      draw( mode ) {
        glVertex2d( 0, 0 )
        glVertex2d( box.width, 0 )
        glVertex2d( box.width, box.height )
        glVertex2d( 0, box.height )
      }

    }

  }

}