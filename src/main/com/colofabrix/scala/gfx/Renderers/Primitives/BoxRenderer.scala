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

package com.colofabrix.scala.gfx.Renderers.Primitives

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.{ Color3D, Renderer }
import org.lwjgl.opengl.GL11


/**
 * Renders a box to the screen
 */
class BoxRenderer( val box: Box, color: Color3D = null ) extends Renderer {

  def render( ): Unit = {

    GL11.glPushMatrix( )

    GL11.glTranslated( box.bottomLeft.x, box.bottomLeft.y, 0 )
    if( color != null ) {
      color.bind( )
    }

    GL11.glBegin( GL11.GL_QUADS )
    GL11.glVertex2d( 0, 0 )
    GL11.glVertex2d( box.width, 0 )
    GL11.glVertex2d( box.width, box.height )
    GL11.glVertex2d( 0, box.height )
    GL11.glEnd( )

    GL11.glPopMatrix( )

  }

}
