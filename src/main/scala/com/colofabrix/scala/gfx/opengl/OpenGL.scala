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

package com.colofabrix.scala.gfx.opengl

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{ Display, DisplayMode }

/**
  * This class is meant to be a simple wrapper for OpenGL, to provide a Scala-way to use graphics but keeping things
  * as simple and small as possible (it doesn't want to be a full Scala OpenGL library)
  */
object OpenGL {

  private val DEG2RAD = 180 / Math.PI

  /** Initialize OpenGL */
  def init(width: Int, height: Int, title: String = "OpenGL Window") = {
    Display.setDisplayMode(new DisplayMode(width, height))
    Display.create()
    Display.setTitle(title)

    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glViewport(0, 0, width, height)

    // Setting up the projection matrix
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    glOrtho(0, width, height, 0, 1, -1)

    // Set matrix for running mode
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
  }

  /** Terminates OpenGL */
  def destroy(): Unit = {
    Display.destroy()
  }

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

  /** Apply a reference frame on top of an existing one */
  def apply(frame: Frame = Frame())(actions: => Unit): Unit = {
    val colourBuffer = BufferUtils.createFloatBuffer(16)

    // Save the current settings (only if needed)
    if (frame.colour.isDefined)
      glGetFloat(GL_CURRENT_COLOR, colourBuffer)
    if (frame.position.isDefined || frame.rotation.isDefined)
      glPushMatrix()

    // Set position, rotation and colour
    for (p <- frame.position)
      glTranslated(p.x, p.y, 0.0)
    for (r <- frame.rotation)
      glRotated(r.ϑ * DEG2RAD, 0, 0, 1)
    for (c <- frame.colour)
      glColor3d(c.r, c.g, c.b)

    actions

    // Restore the previous settings
    if (frame.position.isDefined || frame.rotation.isDefined)
      glPopMatrix()
    if (frame.colour.isDefined)
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