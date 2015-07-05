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

import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.gfx.Color3D
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.integration.TankEvaluator
import com.colofabrix.scala.simulation.{ Bullet, Tank }
import org.lwjgl.opengl.GL11

/**
 * Renders a tank to the screen with its properties
 */
class TankRenderer( tank: Tank ) extends Renderer {

  def render( ): Unit = {

    val world = tank.world

    val size: Double = tank.objectShape.asInstanceOf[Circle].radius
    val fitness: Double = new TankEvaluator( ).getFitness( tank, null )
    val sight: Double = world.max_sight

    GL11.glPushMatrix( )

    // Rotation of the tank
    GL11.glTranslated( tank.position.x, tank.position.y, 0 )
    GL11.glRotated( tank.rotation.t * 180 / Math.PI, 0, 0, 1 )

    // The color of the tank depends on its fitness
    GL11.glColor3d( 1, fitness / TankEvaluator.higherFitness( world ), 0 )

    // Draw the shape of a tank
    GL11.glBegin( GL11.GL_TRIANGLES )
    GL11.glVertex2d( size, 0.0 )
    GL11.glVertex2d( -0.866025 * size, 0.5 * size )
    GL11.glVertex2d( -0.866025 * size, -0.5 * size )
    GL11.glEnd( )
    new CircleRenderer( new Circle( Vector2D.origin, size ) ).render( )

    // Draw the sight of a bullet
    val threatRenderer = tank.sight( classOf[Tank] ).renderer
    threatRenderer.bindColor( new Color3D( 0.3, 0.1, 0.1 ) )
    threatRenderer.render( )

    // Draw the sights of a tank
    val treatRenderer = tank.sight( classOf[Bullet] ).renderer
    treatRenderer.bindColor( new Color3D( 0.1, 0.3, 0.1 ) )
    treatRenderer.render( )

    GL11.glPopMatrix( )
  }

}
