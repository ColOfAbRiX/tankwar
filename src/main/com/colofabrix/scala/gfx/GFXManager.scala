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

package com.colofabrix.scala.gfx

import com.colofabrix.scala.simulation.World
import org.lwjgl.opengl.{GL11, DisplayMode, Display}

/**
 */
class GFXManager (val world: World, windowsTitle: String, val BGRenderer: Renderer){

  require(world != null, "The World must be specified")
  require(BGRenderer != null, "There must be a BG Renderer")

  val width = world.arena.width
  val height = world.arena.height

  // Initialize OpenGL
  Display.setDisplayMode(new DisplayMode(width.toInt, height.toInt))
  Display.create()
  Display.setTitle(windowsTitle)

  // Set the camera
  setCamera()

  def renderAll(): Unit = {

    setCamera()

    BGRenderer.render()

    try {
      world.getRenderers().foreach(r => r.render())
      world.UIManager.getRenderers().foreach(r => r.render())
    }
    catch  {
      case np: NullPointerException =>
    }

    //TODO: Get this from flag
    Display.sync(60)
    Display.update()

    // Deal with a close request
    if( Display.isCloseRequested ) {
      System.exit(0)
    }

  }

  private def setCamera( ) {
    GL11.glClearColor(0f,0f, 0f, 1.0f)
    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT)
    GL11.glMatrixMode(GL11.GL_PROJECTION)
    GL11.glLoadIdentity()
    GL11.glOrtho(0, width, 0, height, -1, 1)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
    GL11.glLoadIdentity()
    GL11.glViewport(0, 0, width.toInt, height.toInt)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
    GL11.glMatrixMode(GL11.GL_PROJECTION)
    GL11.glLoadIdentity()
    GL11.glOrtho(0, Display.getWidth, 0, Display.getHeight, 1, -1)
    GL11.glMatrixMode(GL11.GL_MODELVIEW)
    GL11.glLoadIdentity()


  }

}
