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

package com.colofabrix.scala.gfx

import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.simulation.World
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{ Display, DisplayMode }
import scala.language.reflectiveCalls

/**
 * Runs all of the GFX operations
 */
class GFXManager( val world: World, windowsTitle: String, val BGRenderer: Renderer ) extends Renderer {
  require( world != null, "The World must be specified" )
  require( BGRenderer != null, "There must be a BG Renderer" )

  val height = world.arena.height
  val width = world.arena.width

  /**
   * Render all of the GFX
   *
   * The parameter `create` might be ignored from the implementation, depending on what the renderer is meant
   * to do and usually its behaviour is stated in the documentation
   *
   * @param create Ignored by this method
   */
  def render( create: Boolean = true ): Unit = {

    clearScreen( )

    BGRenderer.render( )

    // Renders the user interaction graphic objects
    for( r <- world.UIManager.renderers )
      r.render( )

    // Renders the graphical objects
    if( _flags.getWithDefault( "render", true ) ) {
      for( r <- world.renderers )
        r.render( )

      Display.sync( _flags.getWithDefault( "sync", 25 ) )
    }

    Display.update( )

    while( _flags.getWithDefault( "pause", false ) ) {
      world.UIManager.update( )
      Thread.sleep( 200 )
    }

    // Deal with a close request
    if( Display.isCloseRequested ) {
      System.exit( 0 )
    }

  }

  // Initialize OpenGL
  Display.setDisplayMode( new DisplayMode( width.toInt, height.toInt ) )
  Display.create( )
  Display.setTitle( windowsTitle )

  glMatrixMode( GL_MODELVIEW )
  glLoadIdentity( )
  glViewport( 0, 0, width.toInt, height.toInt )

  glMatrixMode( GL_PROJECTION )
  glLoadIdentity( )
  glOrtho( 0, Display.getWidth, Display.getHeight, 0, 1, -1 )

  glMatrixMode( GL_MODELVIEW )
  glLoadIdentity( )

  private def _flags = world.UIManager.flags

  private def clearScreen( ): Unit = {
    glClearColor( 0f, 0f, 0f, 1.0f )
    glClear( GL_COLOR_BUFFER_BIT )
  }
}