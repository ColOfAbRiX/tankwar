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

import com.colofabrix.scala.geometry.shapes.Polygon
import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import org.lwjgl.opengl.GL11._

/**
 * Renders a polygon to the screen
 *
 * @param polygon The polygon that has to be rendered
 * @param filled True indicated the circle has to be filled. It defaults to false
 * @param defaultFrame The default frame to use when a new drawing context has to be created
 */
class PolygonRenderer( val polygon: Polygon, filled: Boolean = false, defaultFrame: Frame = Frame( Colour.WHITE ) )
    extends Renderer {

  /**
   * Draws a polygon on the screen
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  def render( create: Boolean = true ): Unit = {

    withDefaultContext( create, defaultFrame ) {

      val mode = if ( filled ) GL_TRIANGLE_FAN else GL_LINE_LOOP
      drawOpenGL( mode ) {
        // Don't forget the edge from the last to the first vertex
        ( polygon.vertices :+ polygon.vertices.head ).foreach( drawVertex )
      }

    }

  }

}