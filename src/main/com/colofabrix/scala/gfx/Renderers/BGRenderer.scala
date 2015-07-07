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
import com.colofabrix.scala.gfx.abstracts.Renderer
import org.lwjgl.opengl.GL11

/**
 * Renders the Background of the screen
 */
class BGRenderer( box: Box ) extends Renderer {

  def render( ): Unit = {
    GL11.glPushMatrix( )

    GL11.glTranslated( 0, 0, 0 )
    GL11.glColor3d( 0, 0, 0 )

    GL11.glBegin( GL11.GL_QUADS )

    GL11.glVertex2d( box.bottomLeft.x, box.bottomLeft.y )
    GL11.glVertex2d( box.bottomLeft.x, box.topRight.y )
    GL11.glVertex2d( box.topRight.x, box.topRight.y )
    GL11.glVertex2d( box.topRight.x, box.bottomLeft.y )

    GL11.glEnd( )

    GL11.glPopMatrix( )
  }

}
