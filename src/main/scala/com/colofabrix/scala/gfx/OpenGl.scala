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

import java.awt.Font

import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.math.Vect
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{ Display, DisplayMode }
import org.newdawn.slick.{ Color, TrueTypeFont }

import scala.collection.immutable.HashMap

/**
  * This class is meant to be a simple wrapper for OpenGL, to provide a Scala-way to use graphics but keeping things
  * as simple and small as possible (it doesn't want to be a full Scala OpenGL library)
  */
object OpenGl {

  /**
    * A colour in the OpenGL system
    */
  final case class Colour( r: Double = 0.0, g: Double = 0.0, b: Double = 0.0 )

  /**
    * An OpenGL reference frame. It holds data about its position to the absolute reference, its rotation and the colour of its brush
    */
  class DrawingFrame( val colour: Option[Colour] = None, val position: Option[Vect] = None, val rotation: Option[Vect] = None )

  final class GlobalFrame( _position: Option[Vect] = None, _rotation: Option[Vect] = None, val scale: Option[Double] ) extends DrawingFrame( None, _position, _rotation )

  object DrawingFrame {

    def apply( colour: Colour = null, position: Vect = null, rotation: Vect = null ): DrawingFrame =
      new DrawingFrame( Option( colour ), Option( position ), Option( rotation ) )

  }

  object GlobalFrame {

    def apply( position: Vect = null, rotation: Vect = null, scale: Double = 1 ): GlobalFrame =
      new GlobalFrame( Option( position ), Option( rotation ), Some( scale ) )

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

  private var fontMap = HashMap[Int, TrueTypeFont]()

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
    * @param mode    What to draw on screen, see OpenGL for more details
    * @param frame   The configuration of the reference frame. If not specified the reference frame is not affected
    * @param actions The function that actually draw
    */
  def drawOpenGL( mode: Int, frame: DrawingFrame = DrawingFrame() )( actions: ⇒ Unit ): Unit = {

    // First sets the reference frame
    applyContext( frame ) {

      // Then starts the drawing context
      glBegin( mode )
      actions
      glEnd()

    }

  }

  /**
    * Draw some text on the screen.
    *
    * @param text      A list of texts to write one after the other
    * @param awtFont   An AWT font used to draw the text
    * @param interline Interline spacing between lines
    * @param frame     The configuration of the reference frame. If not specified the reference frame is not affected
    */
  def drawText( text: List[String], awtFont: Font, interline: Double = 1.5, frame: DrawingFrame = DrawingFrame() ) = {
    val font = getTTFont( awtFont )

    applyContext( frame ) {
      // Slick fonts don't work like OpenGL. I retrieve the current colour from the OpenGL
      val colourBuffer = BufferUtils.createFloatBuffer( 16 )
      glGetFloat( GL_CURRENT_COLOR, colourBuffer )
      val defaultTextColour = Colour( colourBuffer.get( 0 ).toDouble, colourBuffer.get( 1 ).toDouble, colourBuffer.get( 2 ).toDouble )
      val color = frame.colour.getOrElse( defaultTextColour )
      val slickColor = new Color( color.r.toFloat, color.g.toFloat, color.b.toFloat, 1 )

      // Draw all the lines of text
      text.zipWithIndex.foreach {
        case ( t, i ) ⇒
          font.drawString( 0, ( awtFont.getSize * interline * i ).toFloat, t, slickColor )
      }
    }
  }

  /**
    * Get a TrueTypeFont from a cache
    *
    * Loading fonts is expensive, so a simple caching system provides faster access
    *
    * @param awtFont The AWT font of which you want to obtain the TrueTypeFont
    * @return
    */
  private def getTTFont( awtFont: Font ): TrueTypeFont = {
    if ( !fontMap.contains( awtFont.hashCode ) ) {
      val ttfont = new TrueTypeFont( awtFont, false )

      fontMap = fontMap + ( awtFont.hashCode → ttfont )
      return ttfont
    }

    fontMap( awtFont.hashCode )
  }

  /**
    * Apply a reference frame on top of an existing one
    *
    * The method sets position, rotation and brush colour for the active reference frame. Position and rotation
    * are treated as relative to
    * For the way this function is built, if a value of the `frame` is not provided, that configuration
    * will not change. This allow to nest subsequent frame modifications from `drawingContext`, `setFrame`
    * and `draw`.
    *
    * @param frame   The configuration of the reference frame. If not specified the reference frame is not affected
    * @param actions The function that actually draw
    */
  def applyContext( frame: DrawingFrame = DrawingFrame() )( actions: ⇒ Unit ): Unit = {
    // Save the previous settings (only if needed)
    val colourBuffer = BufferUtils.createFloatBuffer( 16 )
    if ( frame.colour.isDefined ) glGetFloat( GL_CURRENT_COLOR, colourBuffer )
    if ( frame.position.isDefined || frame.rotation.isDefined ) glPushMatrix()

    // Set position, rotation and colour
    for ( p ← frame.position ) glTranslated( p.x, p.y, 0.0 )
    for ( r ← frame.rotation ) glRotated( r.ϑ * DEG2RAD, 0, 0, 1 )
    for ( c ← frame.colour ) glColor3d( c.r, c.g, c.b )

    // Call the actions
    actions

    // Restore the previous settings
    if ( frame.position.isDefined || frame.rotation.isDefined ) glPopMatrix()
    if ( frame.colour.isDefined ) glColor3d( colourBuffer.get( 0 ).toDouble, colourBuffer.get( 1 ).toDouble, colourBuffer.get( 2 ).toDouble )
  }

  /**
    * Draw a vertex on the screen.
    *
    * What it draws is subordinates to the drawing mode in the drawing context
    *
    * @param vertex The vector identifying the vertex
    */
  def drawVertex( vertex: Vect ): Unit = {
    glVertex2d( vertex.x, vertex.y )
  }

  /**
    * Creates a new text context.
    *
    * The method encloses the action between the necessary operations to draw a text
    * For the way this function is built, if a value of the `frame` is not provided, that configuration
    * will not change. This allow to nest subsequent frame modifications from `drawingContext`, `setFrame`
    * and `draw`.
    *
    * @param actions The drawing actions
    */
  def textContext()( actions: ⇒ Unit ): Unit = {
    // Enable alpha blending to merge text and graphics
    glEnable( GL_BLEND )
    glBlendFunc( GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA )

    actions

    glDisable( GL_BLEND )
  }

  /**
    * Uses a drawing context if provided or creates a new one
    *
    * This method is used to override the default behaviour of creating a new context. It is useful to allow
    * the caller to set the context for the new objects that are going to be drawn.
    *
    * @param create    With a value of true a new drawing context will be create, with false nothing is done
    * @param withFrame The configuration of the reference frame if a new context has to be created. If not specified the reference frame is not affected
    * @param actions   The drawing actions
    */
  def withDefaultContext( create: Boolean, withFrame: DrawingFrame = DrawingFrame() )( actions: ⇒ Unit ): Unit = {

    if ( create ) {
      applyContext( withFrame ) {
        actions
      }
    }
    else {
      actions
    }

  }

  def drawCircle( circle: Circle, filled: Boolean = false ): Unit = {

    val frame = DrawingFrame(
      position = circle.center
    )

    applyContext( frame ) {
      val a = 1

      if ( filled ) {
        var x2, y2: Double = 0
        glBegin( GL_TRIANGLE_FAN )
        for ( angle ← 0d to ( 2d * Math.PI ) by 0.01 ) {
          x2 = Math.sin( angle ) * circle.radius
          y2 = Math.cos( angle ) * circle.radius
          glVertex2d( x2, y2 )
        }
        glEnd()
      }
      else {
        applyContext( frame ) {
          glBegin( GL_LINE_LOOP )
          for ( i ← 0 to 360 ) {
            val deginrad = i * Math.PI / 180
            glVertex2d( Math.cos( deginrad ) * circle.radius, Math.sin( deginrad ) * circle.radius )
          }
          glEnd()
        }
      }
    }

  }

  def initOpenGl( width: Int, height: Int, title: String = "OpenGl Window" ): GlobalFrame = {

    // Initialize OpenGL
    Display.setDisplayMode( new DisplayMode( width, height ) )
    Display.create()
    Display.setTitle( title )

    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()
    glViewport( 0, 0, width.toInt, height.toInt )

    glMatrixMode( GL_PROJECTION )
    glLoadIdentity()
    glOrtho( 0, Display.getWidth.toDouble, Display.getHeight.toDouble, 0, 1, -1 )

    glMatrixMode( GL_MODELVIEW )
    glLoadIdentity()

    GlobalFrame()

  }

  def frameClearUp() = {
    glClearColor( 0f, 0f, 0f, 1.0f )
    glClear( GL_COLOR_BUFFER_BIT )

    if ( Display.isCloseRequested ) {
      System.exit( 0 )
    }

  }

}