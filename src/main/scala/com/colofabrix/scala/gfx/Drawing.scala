/*
 * Copyright (C) 2017 Fabrizio Colonna
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

package com.colofabrix.scala.gfx

import java.awt.Font
import java.lang.Math._
import scala.collection.immutable.HashMap
import com.colofabrix.scala.math.{ Vect, XYVect }
import org.lwjgl.opengl.GL11._
import org.newdawn.slick.{ Color, TrueTypeFont }

object Drawing {

  private var fontMap = HashMap[Int, TrueTypeFont]()

  /** Get a TrueTypeFont from a cache */
  private def getTTFont(awtFont: Font): TrueTypeFont = {
    if( !fontMap.contains(awtFont.hashCode) ) {
      val ttfont = new TrueTypeFont(awtFont, false)
      fontMap = fontMap + (awtFont.hashCode → ttfont)
      return ttfont
    }

    fontMap(awtFont.hashCode)
  }

  /** Draw a vertex on the screen. */
  def drawVertex(vertex: Vect): Unit = {
    glVertex2d(vertex.x, vertex.y)
  }

  /** Draw a polygon. */
  def drawPolygon(vertices: Seq[Vect], filled: Boolean = false): Unit = {
    val mode = if( filled ) GL_POLYGON else GL_QUADS
    OpenGL.draw(mode) {
      for( v <- vertices ) {glVertex2d(v.x, v.y) }
    }
  }

  /** Draw a circle. */
  def drawCircle(center: Vect, radius: Double, filled: Boolean = false, precision: Double = 0.1): Unit = {
    val mode = if( filled ) GL_TRIANGLE_FAN else GL_LINE_LOOP
    OpenGL.draw(mode) {

      for( angle ← 0d to (2d * PI) by precision ) {
        glVertex2d(
          sin(angle) * radius + center.x,
          cos(angle) * radius + center.y
        )
      }

    }
  }

  /** Draw a line segment. */
  def drawSegment(p0: Vect, p1: Vect) = {
    OpenGL.draw(GL_LINES) {
      drawVertex(p0)
      drawVertex(p1)
    }
  }

  /** Draw some text on the screen. */
  def drawText(text: List[String], awtFont: Font, interline: Double = 1.5, colour: Colour = Colour.WHITE): Unit = {
    val font = getTTFont(awtFont)

    // Slick fonts don't work like OpenGL. I retrieve the current colour from the OpenGL
    val slickColor = new Color(colour.r.toFloat, colour.g.toFloat, colour.b.toFloat, 1)

    // Draw all the lines of text
    OpenGL.draw(GL_QUADS) {
      for( (t, i) ← text.zipWithIndex ) {
        font.drawString(0, (awtFont.getSize * interline * i).toFloat, t, slickColor)
      }
    }
  }

  /** Draws a vector. */
  def drawVector(v: Vect, tail: Vect = Vect.zero, size: Double = 5.0) = {
    OpenGL.apply(position = Some(tail)) {
      // Main line
      OpenGL.draw(GL_LINES) {
        glVertex2d(0.0, 0.0)
        glVertex2d(v.x, v.y)
      }

      // Head of the vector
      val arrowSize = Math.min(size, v.ρ / 4.0)
      OpenGL.apply(position = Some(v), rotation = Some(v)) {
        OpenGL.draw(GL_TRIANGLES) {
          glVertex2d(arrowSize, 0.0)
          glVertex2d(-0.866025 * arrowSize, 0.5 * arrowSize)
          glVertex2d(-0.866025 * arrowSize, -0.5 * arrowSize)
        }
      }
    }

  }
}
