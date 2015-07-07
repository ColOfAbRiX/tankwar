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

import com.colofabrix.scala.math.Vector2D
import org.lwjgl.opengl.GL11._

/**
 * This class is meant to be a simple wrapper for OpenGL, to provide a Scala-way to use graphics but keeping things
 * as simple and small as possible (it doesn't want to be a full Scala OpenGL library)
 */
object OpenGL {


  /**
   * A colour in the OpenGL system
   */
  case class Colour( r: Double = 0.0, g: Double = 0.0, b: Double = 0.0 )

  /**
   * An OpenGL reference frame. It holds data about its position to the absolute reference, its rotation and the colour of its brush
   *
   * @param _colour The colour of the brush
   * @param _position The position of the reference frame relative to the absolute frame
   * @param _rotation The rotation vector of the reference frame relative to the absolute frame
   */
  case class Frame( private val _colour: Colour = null, private val _position: Vector2D = null, private val _rotation: Vector2D = null ) {
    /**
     * The colour of the brush
     */
    def colour = Option( _colour )

    /**
     * The position of the reference frame relative to the absolute frame
     */
    def position = Option( _position )

    /**
     * The rotation vector of the reference frame relative to the absolute frame
     */
    def rotation = Option( _rotation )
  }

  /**
   * Predefined colour set
   */
  object Colour {
    val BLACK = Colour( 0.0, 0.0, 0.0 )
    val BLUE = Colour( 0.0, 0.0, 1.0 )
    val CYAN = Colour( 0.0, 1.0, 1.0 )
    val DARK_BLUE = Colour( 0.1, 0.1, 0.3 )
    val DARK_CYAN = Colour( 0.0, 0.3, 0.3 )
    val DARK_GREEN = Colour( 0.1, 0.2, 0.1 )
    val DARK_GREY = Colour( 0.2, 0.2, 0.2 )
    val DARK_MAGENTA = Colour( 0.3, 0.0, 0.3 )
    val DARK_RED = Colour( 0.3, 0.1, 0.1 )
    val DARK_YELLOW = Colour( 0.3, 0.3, 0.0 )
    val GREEN = Colour( 0.0, 1.0, 0.0 )
    val LIGHT_BLUE = Colour( 0.8, 0.8, 1.0 )
    val LIGHT_CYAN = Colour( 8.0, 1.0, 1.0 )
    val LIGHT_GREEN = Colour( 0.8, 1.0, 0.8 )
    val LIGHT_GREY = Colour( 0.8, 0.8, 0.8 )
    val LIGHT_MAGENTA = Colour( 1.0, 0.8, 1.0 )
    val LIGHT_RED = Colour( 1.0, 0.8, 0.8 )
    val LIGHT_YELLOW = Colour( 1.0, 1.0, 0.8 )
    val MAGENTA = Colour( 1.0, 0.0, 1.0 )
    val RED = Colour( 1.0, 0.0, 0.0 )
    val WHITE = Colour( 1.0, 1.0, 1.0 )
    val YELLOW = Colour( 1.0, 1.0, 0.0 )
  }

  private val DEG2RAD = 180 / Math.PI

  /**
   * Initialize a drawing action
   *
   * The method sets a reference frame, if specified and then groups drawing actions inside
   * the appropriate OpenGL primitives
   * For the way this function is built, if a value of the `frame` is not provided, that configuration
   * will not change. This allow to nest subsequent frame modifications from `drawingContext`, `setFrame`
   * and `draw`.
   *
   * @see https://www.opengl.org/sdk/docs/man2/xhtml/glBegin.xml
   * @param mode What to draw on screen, see OpenGL for more details
   * @param frame The configuration of the reference frame. If not specified the reference frame is not affected
   * @param actions The function that actually draw
   */
  def draw( mode: Int, frame: Frame = Frame( ) )( actions: => Unit ) {

    // First sets the reference frame
    setFrame( frame ) {

      // Then starts the drawing context
      glBegin( mode )
      actions
      glEnd( )

    }

  }

  /**
   * Draw a vertex on the screen.
   *
   * What it draws is subordinates to the drawing mode in the drawing context
   *
   * @param vertex The vector identifying the vertex
   */
  def drawVertex( vertex: Vector2D ): Unit = {
    glVertex2d( vertex.x, vertex.y )
  }

  /**
   * Uses a drawing context if provided or creates a new one
   *
   * This method is used to override the default behaviour of creating a new context. It is useful to allow
   * the caller to set the context for the new objects that are going to be drawn.
   *
   * @param create With a value of true a new drawing context will be create, with false nothing is done
   * @param frame The configuration of the reference frame. If not specified the reference frame is not affected
   * @param actions The drawing actions
   */
  def withContext( create: Boolean, frame: Frame = Frame( ) )( actions: => Unit ): Unit = {

    if( create ) {
      drawingContext( frame ) {
        actions
      }
    }
    else {
      actions
    }

  }

  /**
   * Creates a new drawing context.
   *
   * The method encloses the action between a push/pop of the matrix stack and it also resets the reference frame
   * For the way this function is built, if a value of the `frame` is not provided, that configuration
   * will not change. This allow to nest subsequent frame modifications from `drawingContext`, `setFrame`
   * and `draw`.
   *
   * @param frame The configuration of the reference frame. If not specified the reference frame is set to default
   * @param actions The drawing actions
   */
  def drawingContext( frame: Frame = Frame( Colour.BLACK, Vector2D.zero, Vector2D.zero ) )( actions: => Unit ): Unit = {
    glPushMatrix( )

    setFrame( frame ) {
      actions
    }

    glPopMatrix( )
  }

  /**
   * Sets a reference frame
   *
   * The method sets position, rotation and brush colour for the active reference frame.
   * For the way this function is built, if a value of the `frame` is not provided, that configuration
   * will not change. This allow to nest subsequent frame modifications from `drawingContext`, `setFrame`
   * and `draw`.
   *
   * @param frame The configuration of the reference frame. If not specified the reference frame is not affected
   * @param actions The function that actually draw
   */
  def setFrame( frame: Frame = Frame( ) )( actions: => Unit ): Unit = {
    // Set position, rotation and colour
    for( p <- frame.position ) glTranslated( p.x, p.y, 0.0 )
    for( r <- frame.rotation ) glRotated( r.t * DEG2RAD, 0, 0, 1 )
    for( c <- frame.colour ) glColor3d( c.r, c.g, c.b )

    // Call the actions
    actions
  }

}
