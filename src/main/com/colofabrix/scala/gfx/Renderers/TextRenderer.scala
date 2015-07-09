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
import org.newdawn.slick.TrueTypeFont

/**
 *
 */
class TextRenderer(
  text: List[String],
  position: Vector2D,
  awtFont: Font = new Font( "Consolas", Font.PLAIN, 12 ) )
  extends Renderer {

  val font = new TrueTypeFont( awtFont, false )

  def render( create: Boolean = true ): Unit = {

    var i = 0;

    withTextContext( ) {
      text.foreach(t => {
        font.drawString(position.x.toFloat, position.y.toFloat+20*i, t, Colour.CYAN.asSlickColour)
        i+=1
      })
    }

  }

}
