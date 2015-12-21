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
import com.colofabrix.scala.gfx.abstracts.{ Renderable, Renderer }
import scala.language.implicitConversions

/**
 * Writes some text to the screen
 *
 * @param text A list of strings to be written on the screen one after the other
 * @param awtFont The font used to draw the text
 * @param interline The line height, expressed relative to the font size
 * @param defaultFrame The default frame to use when a new drawing context has to be created
 */
class TextRenderer(
  text: List[String],
  defaultFrame: Frame = Frame( Colour.WHITE ),
  awtFont: Font = new Font( "Consolas", Font.PLAIN, 12 ),
  interline: Double = 1.5
) extends Renderer {

  /**
   * Writes some text to the screen
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   */
  def render( create: Boolean = true ): Unit = {

    withDefaultContext( create, defaultFrame ) {

      textContext( ) {

        drawText( text, awtFont, interline )

      }

    }

  }

}

object TextRenderer {

  // scalastyle:off structural.type
  /**
   * Implicit method to convert a text into a renderable object
   *
   * @param text The text to convert
   * @return A new Renderable object that returns a new `TextRenderer`
   */
  implicit def String2Renderable( text: String ): Renderable with Object {def renderer: Renderer} =
    new com.colofabrix.scala.gfx.abstracts.Renderable( ) {
      override def renderer: Renderer = new TextRenderer( List( text ) )
    }

  /**
   * Implicit method to convert a text list into a renderable object
   *
   * @param text A list of strings to convert into a renderable object
   * @return A new Renderable object that returns a new `TextRenderer`
   */
  implicit def StringList2Renderable( text: List[String] ): Renderable with Object {def renderer: Renderer} =
    new com.colofabrix.scala.gfx.abstracts.Renderable( ) {
      override def renderer: Renderer = new TextRenderer( text )
    }
  // scalastyle:on structural.type

}