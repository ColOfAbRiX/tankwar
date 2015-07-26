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

import java.awt.Font

import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.integration.TankEvaluator
import com.colofabrix.scala.simulation.{ Bullet, Tank }
import org.lwjgl.opengl.GL11._


/**
 * Renders a tank to the screen with its properties
 *
 * @param tank The tank that has to be drawn
 */
class TankRenderer( tank: Tank ) extends Renderer {

  private val size: Double = tank.objectShape.asInstanceOf[Circle].radius

  /**
   * Draws a Tank n the screen
   *
   * @param create This parameter is disabled for this renderer
   */
  def render( create: Boolean = true ): Unit = {
    val fitness: Double = new TankEvaluator( ).getFitness( tank, null )
    val colour = Colour( 1.0, fitness / Math.max( Double.MinPositiveValue, TankEvaluator.higherFitness( tank.world ) ), 0.0 )
    val flags = tank.world.UIManager.flags

    drawingContext( Frame( _position = tank.position ) ) {

      textContext() {
        drawText( List( TankEvaluator.fitness( tank ).toString ), new Font( "Consolas", Font.PLAIN, 12 ) )
      }

      // Drawing tank's main shape (a triangle surrounded by a circle)
      setFrame( Frame( colour ) ) {

        // Draw the tank's main triangle
        draw( GL_TRIANGLES, Frame( _rotation = tank.rotation ) ) {
          glVertex2d( size, 0.0 )
          glVertex2d( -0.866025 * size, 0.5 * size )
          glVertex2d( -0.866025 * size, -0.5 * size )
        }

        // Draw a circle corresponding to its actual size
        new Circle( Vector2D.zero, size ).renderer.render( false )

      }

      if( flags.getWithDefault( "tksight", true ) ) {

        // Draw the sight toward bullets
        setFrame( Frame( Colour.DARK_RED ) ) {
          tank.sight( classOf[Bullet] ).renderer.render( false )
        }

        // Draw the sight toward tanks
        setFrame( Frame( Colour.DARK_GREEN ) ) {
          tank.sight( classOf[Tank] ).renderer.render( false )
        }

      }

    }

    if( flags.getWithDefault( "vectors", true ) ) {

      // Draw the speed vector
      setFrame( Frame( Colour.WHITE ) ) {
        new VectorRenderer( tank.speed * 10, tank.position ).render( )
      }

      // Draw bullet sight vectors
      val (pos, speed) = tank.calculateBulletVision
      setFrame( Frame( Colour.CYAN ) ) {
        new VectorRenderer( pos * 10, tank.position ).render( )
      }
      setFrame( Frame( Colour.MAGENTA ) ) {
        new VectorRenderer( speed * 10, tank.position ).render( )
      }

    }

  }

}