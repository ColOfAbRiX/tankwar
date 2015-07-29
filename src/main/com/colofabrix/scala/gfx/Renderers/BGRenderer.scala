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
import com.colofabrix.scala.gfx.OpenGL.{ Colour, Frame }
import com.colofabrix.scala.gfx.abstracts.Renderer


/**
 * Renders the Background of the screen.
 *
 * It's an alias for `BoxRenderer`
 */
class BGRenderer( box: Box, colour: Colour = Colour.BLACK ) extends Renderer {

  /**
   * Renders the background as a box
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  def render( create: Boolean = true ): Unit = new BoxRenderer( box, true, Frame( colour ) ).render( true )

}
