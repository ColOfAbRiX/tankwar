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

package com.colofabrix.scala.gfx.renderers

import java.awt.Font

import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.Vector2D

/**
 * Writes some text to the screen
 *
 * @param text A list of strings to be written on the screen one after the other
 * @param position A vector indicating the top-left corner (?) of the text
 * @param awtFont The font used to draw the text
 */
class TextRenderer(
  text: List[String],
  position: Vector2D,
  awtFont: Font = new Font( "Consolas", Font.PLAIN, 12 ),
  interline: Double = 1.5,
  colour: Colour = Colour.CYAN
) extends Renderer {

  /**
   * Writes some text to the screen
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  def render( create: Boolean = true ): Unit = {

    withContext( create, Frame( colour, position ) ) {

      textContext( ) {

        drawText( text, awtFont, interline, Frame( Colour.CYAN ) )

      }

    }

  }

}
