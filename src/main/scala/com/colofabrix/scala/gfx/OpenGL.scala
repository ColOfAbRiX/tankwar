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

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vect
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{ Display, DisplayMode }

/**
  * This class is meant to be a simple wrapper for OpenGL, to provide a Scala-way to use graphics but keeping things
  * as simple and small as possible (it doesn't want to be a full Scala OpenGL library)
  */
object OpenGL {

  private
  val DEG2RAD = 180 / Math.PI

  /** Initialize OpenGL */
  def init(width: Int, height: Int, title: String = "OpenGL Window") = {
    Display.setDisplayMode(new DisplayMode(width, height))
    Display.create()
    Display.setTitle(title)

    // Set viewport
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glViewport(0, 0, width, height)

    // Setting up the camera
    setCamera(Box(width, height))
  }

  /** Terminates OpenGL */
  def destroy(): Unit = Display.destroy()

  /** Updates a display */
  def update(): Unit = {
    Display.update()
  }

  /** Initialize a drawing action */
  def draw(mode: Int)(actions: => Unit): Unit = {
    glBegin(mode)
    actions
    glEnd()
  }

  /** Set the projection matrix */
  def setCamera(viewport: Box): Unit = {
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(
      viewport.origin.x, viewport.opposite.x,
      viewport.origin.y, viewport.opposite.y,
      1, -1
    )

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
  }

  /** Apply a reference frame on top of an existing one */
  def apply(
    colour: Option[Colour] = None,
    position: Option[Vect] = None,
    rotation: Option[Vect] = None,
    lineWidth: Double = 1.0
  )(actions: => Unit): Unit = {
    val colourBuffer = BufferUtils.createFloatBuffer(16)

    // Save the current settings (only if needed)
    if( colour.isDefined )
      glGetFloat(GL_CURRENT_COLOR, colourBuffer)
    if( position.isDefined || rotation.isDefined )
      glPushMatrix()

    // Set position, rotation and colour
    for( p <- position )
      glTranslated(p.x, p.y, 0.0)
    for( r <- rotation )
      glRotated(r.ϑ * DEG2RAD, 0, 0, 1)
    for( c <- colour )
      glColor3d(c.r, c.g, c.b)
    glLineWidth(lineWidth.toFloat)

    actions

    // Restore the previous settings
    if( position.isDefined || rotation.isDefined )
      glPopMatrix()
    if( colour.isDefined )
      glColor3d(
        colourBuffer.get(0).toDouble,
        colourBuffer.get(1).toDouble,
        colourBuffer.get(2).toDouble
      )
  }

  /** Creates a new text context. */
  def text()(actions: => Unit): Unit = {
    // Enable alpha blending to merge text and graphics
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)

    actions

    glDisable(GL_BLEND)
  }

  def clear() = {
    glClearColor(0f, 0f, 0f, 1.0f)
    glClear(GL_COLOR_BUFFER_BIT)
  }

}