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

package com.colofabrix.scala.gfx.Renderers

import com.colofabrix.scala.gfx.Renderer
import org.lwjgl.opengl.GL11

/**
 * Renders the BackGround to the screen
 */
class BGRenderer( width: Int, height: Int ) extends Renderer {

  def render( ): Unit = {
    GL11.glPushMatrix( )

    GL11.glTranslated( 0, 0, 0 )
    GL11.glColor3d( 0, 0, 0 )

    GL11.glBegin( GL11.GL_QUADS )

    GL11.glVertex2d( 0, 0 )
    GL11.glVertex2d( 0, height )
    GL11.glVertex2d( width, height )
    GL11.glVertex2d( width, 0 )

    GL11.glEnd( )

    GL11.glPopMatrix( )
  }

}
