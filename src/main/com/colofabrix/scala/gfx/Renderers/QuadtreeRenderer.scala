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

import com.colofabrix.scala.geometry.abstracts.{ Quadtree, Shape }
import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**
  */
class QuadtreeRenderer( val quadtree: Quadtree[_ <: Shape, _ <: PhysicalObject] ) extends Renderer {
  /**
   * Draw the appropriate things on the screen given a specific drawing context
   *
   * The parameter `create` might be ignored from the implementation, depending on what the renderer is meant
   * to do and usually its behaviour is stated in the documentation
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  override def render( create: Boolean ): Unit = {

    withContext( create, Frame( Colour.DARK_GREY ) ) {

      // Renders the current boundaries
      quadtree.bounds.renderer.render( )
      for( n <- quadtree.nodes )
        new QuadtreeRenderer( n ).render( false )

    }

  }

}
