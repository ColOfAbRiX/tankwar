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

import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.gfx.{ Color3D, Renderer }
import com.colofabrix.scala.math.Vector2D
import org.lwjgl.opengl.GL11


/**
 *
 * Renders a Circle to the screen
 *
 */
class CircleRenderer( val circle: Circle, color: Color3D = null, val filled: Boolean = false ) extends Renderer {

  def render( ): Unit = {
    if( filled ) {
      render_filled( )
    }
    else {
      render_empty( )
    }
  }

  private def render_filled( ): Unit = {
    GL11.glPushMatrix( )
    GL11.glTranslated( circle.center.x, circle.center.y, 0 )
    GL11.glScaled( circle.radius, circle.radius, 1 )

    if( color != null ) {
      color.bind( )
    }

    val numSegments: Int = Math.max( (circle.radius * 2.0 * Math.PI / 10).toInt, 10 )

    GL11.glBegin( GL11.GL_TRIANGLE_FAN )
    GL11.glVertex2f( 0, 0 )
    var i: Int = 0
    while( i <= numSegments ) {
      {
        val angle: Double = Math.PI * 2 * i / numSegments
        GL11.glVertex2f( Math.cos( angle ).toFloat, Math.sin( angle ).toFloat )
      }
      i += 1
      i - 1
    }
    GL11.glEnd( )

    GL11.glPopMatrix( )
  }

  private def render_empty( ): Unit = {
    val numSegments: Int = Math.max( (circle.radius * 2.0 * Math.PI / 10).toInt, 10 )

    GL11.glPushMatrix( )

    GL11.glTranslated( 0, 0, 0 )
    if( color != null ) {
      color.bind( )
    }
    GL11.glBegin( GL11.GL_LINE_LOOP )

    for( i ← 0 until numSegments ) {
      val tetha = 2.0 * Math.PI * i.toDouble / numSegments.toDouble
      val point = Vector2D.new_rt( circle.radius, tetha )
      GL11.glVertex2f( point.x.toFloat, point.y.toFloat )
    }

    GL11.glEnd( )

    GL11.glPopMatrix( )
  }

}
