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

import com.colofabrix.scala.geometry.shapes.{ Box, Circle }
import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.{ Vect, XYVect, _ }
import com.colofabrix.scala.simulation.integration.TankEvaluator
import com.colofabrix.scala.simulation.{ Bullet, Tank }
import org.lwjgl.opengl.GL11._

import scala.collection.JavaConverters._
import scala.language.reflectiveCalls

/**
  * Renders a tank to the screen with its properties
  *
  * @param tank The tank that has to be drawn
  */
class TankRenderer( tank: Tank ) extends Renderer {

  private val size: Double = tank.objectShape.container match {
    case c: Circle ⇒ c.radius
    case b: Box ⇒ Math.max( b.width, b.height )
    case _ ⇒ throw new IllegalArgumentException( "Unexpected container shape" )
  }

  // scalastyle:off method.length

  /**
    * Draws a Tank n the screen
    *
    * @param create This parameter is disabled for this renderer
    */
  def render( create: Boolean = true ): Unit = {
    @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Nothing" ) )
    val flags = tank.world.UIManager.flags

    val fitness: Double = new TankEvaluator().getFitness( tank, List[Tank]().asJava )
    val colour = Colour( 1.0, fitness / Math.max( FP_PRECISION, TankEvaluator.higherFitness( tank.world ) ), 0.0 )

    applyContext( Frame( _position = tank.position ) ) {

      // Drawing tank's main shape (a triangle surrounded by a circle)
      applyContext( Frame( colour ) ) {

        // Draw the tank's main triangle
        drawOpenGL( GL_TRIANGLES, Frame( _rotation = tank.rotation ) ) {
          glVertex2d( size, 0.0 )
          glVertex2d( -0.866025 * size, 0.5 * size )
          glVertex2d( -0.866025 * size, -0.5 * size )
        }

        // Draw a circle corresponding to its actual size
        new Circle( Vect.zero, size ).renderer.render( false )

      }

      if ( flags.getWithDefault( "tkinfo", true ) ) {

        // Write information about the tank
        applyContext( Frame( Colour.WHITE, XYVect( 10, 10 ) ) ) {
          val fitness = TankEvaluator.fitness( tank ).toString
          val tankName = tank.toString.replaceFirst( "^\\w+@", "" )
          val tmp = tank.calculateBulletVision._1.ρ.toString
          new TextRenderer( List( tankName, fitness ), interline = 1.0 ).render( false )
        }

      }

    }

    if ( flags.getWithDefault( "tksight", true ) ) {

      // Draw the sight toward bullets
      applyContext( Frame( Colour.DARK_RED ) ) {
        tank.sight( classOf[Bullet] ).renderer.render( false )
      }

      // Draw the sight toward tanks
      applyContext( Frame( Colour.DARK_GREEN ) ) {
        tank.sight( classOf[Tank] ).renderer.render( false )
      }

    }

    if ( flags.getWithDefault( "vectors", true ) ) {

      // Draw the speed vector
      applyContext( Frame( Colour.WHITE ) ) {
        new VectorRenderer( tank.speed * 5, tank.position ).render( false )
      }

      // Draw bullet sight vectors
      val ( bPos, bSpeed ) = tank.calculateBulletVision
      applyContext( Frame( Colour.CYAN ) ) {
        new VectorRenderer( bPos * 25, tank.position ).render( false )
      }
      applyContext( Frame( Colour.GREEN ) ) {
        new VectorRenderer( bSpeed * 25, tank.position ).render( false )
      }

      val cPos = tank.calculateClosestBulletVision._1
      applyContext( Frame( Colour.RED ) ) {
        new VectorRenderer( cPos * 25, tank.position ).render( false )
      }

      // Draw tank sight vectors
      val ( tPos, tSpeed ) = tank.calculateTankVision
      applyContext( Frame( Colour.YELLOW ) ) {
        new VectorRenderer( tPos * 25, tank.position ).render( false )
      }
      applyContext( Frame( Colour.MAGENTA ) ) {
        new VectorRenderer( tSpeed * 25, tank.position ).render( false )
      }
    }

  }

  // scalastyle:on method.length

}