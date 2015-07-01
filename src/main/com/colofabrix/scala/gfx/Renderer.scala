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

import com.colofabrix.scala.geometry.shapes.Circle
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.integration.TankEvaluator
import com.colofabrix.scala.simulation.{Bullet, Tank, World}
import org.lwjgl.opengl.{Display, DisplayMode, GL11}

/**
 * This class is used to render the game world, it has only one
 * public method which is to run the entire render process
 *
 * @param world - The world object that the class should work from to get the tanks/bullets to render
 */
class Renderer( val world: World, windowsTitle: String ) {
  require(world != null, "The World must be specified")

  private val width = world.arena.width
  private val height = world.arena.height

  // Initialize OpenGL
  Display.setDisplayMode(new DisplayMode(width.toInt, height.toInt))
  Display.create()
  Display.setTitle(windowsTitle)

  // Set the camera
  setCamera()

  /**
   * Main update function calls the entire render process
   * Should be called once per tick
   */
  def update( ) {
    setCamera()
    drawBackground()
    drawTanks()
    drawBullets()
    drawGUI()
    Display.sync(25)
    Display.update()

    // Deal with a close request
    if( Display.isCloseRequested ) {
      System.exit(0)
    }
  }

  private def drawGUI( ): Unit = {
    world.inputManager.buttons.foreach { btn =>
      btn.render()
    }
  }

  private def setCamera( ) {
    GL11.glClearColor(1f, 1f, 1f, 1.0f)
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

  private def drawTanks( ) {
    world.tanks.filter(!_.isDead).foreach { t => drawTank(t) }
  }

  private def drawTank( tank: Tank ) {
    val size: Double = tank.objectShape.asInstanceOf[Circle].radius
    val fitness: Double = new TankEvaluator().getFitness(tank, null)
    val sight: Double = world.max_sight

    GL11.glPushMatrix()

    // Rotation of the tank
    GL11.glTranslated(tank.position.x, tank.position.y, 0)
    GL11.glRotated(tank.rotation.t * 180 / Math.PI, 0, 0, 1)

    // The color of the tank depends on its fitness
    GL11.glColor3d(1, fitness / TankEvaluator.higherFitness(world), 0)

    // Draw the shape of a tank
    GL11.glBegin(GL11.GL_TRIANGLES)
    GL11.glVertex2d(size, 0.0)
    GL11.glVertex2d(-0.866025 * size, 0.5 * size)
    GL11.glVertex2d(-0.866025 * size, -0.5 * size)
    GL11.glEnd()
    drawCircle(new Circle(Vector2D.origin, size))

    // Draw the sights of a tank
    GL11.glColor3d(0.3, 0.1, 0.1)
    drawCircle(tank.sight(classOf[Bullet]).asInstanceOf[Circle])

    GL11.glColor3d(0.1, 0.3, 0.1)
    drawCircle(tank.sight(classOf[Tank]).asInstanceOf[Circle])

    GL11.glPopMatrix()
  }

  private def drawBullets( ) {
    world.bullets.foreach { b => drawBullet(b) }
  }

  private def drawBullet( bullet: Bullet ) {
    val size: Double = bullet.objectShape.asInstanceOf[Circle].radius

    GL11.glPushMatrix()

    GL11.glTranslated(bullet.position.x, bullet.position.y, 0)
    GL11.glColor3d(0, 0, 1)

    GL11.glBegin(GL11.GL_QUADS)

    GL11.glVertex2d(-size, -size)
    GL11.glVertex2d(-size, size)
    GL11.glVertex2d(size, size)
    GL11.glVertex2d(size, -size)

    GL11.glEnd()

    GL11.glPopMatrix()
  }

  private def drawBackground( ) {
    GL11.glPushMatrix()

    GL11.glTranslated(0, 0, 0)
    GL11.glColor3d(0, 0, 0)

    GL11.glBegin(GL11.GL_QUADS)

    GL11.glVertex2d(0, 0)
    GL11.glVertex2d(0, height)
    GL11.glVertex2d(width, height)
    GL11.glVertex2d(width, 0)

    GL11.glEnd()

    GL11.glPopMatrix()
  }

  /**
   * Draws an approximate Circle Circle
   *
   * Draws a circle as a regular polygon with a specific number of edges
   * Ref: http://slabode.exofire.net/circle_draw.shtml
   *
   * @param circle The circle to draw
   */
  private def drawCircle( circle: Circle ) {
    val numSegments: Int = Math.max((circle.radius * 2.0 * Math.PI / 8).toInt, 12)

    //GL11.glTranslated(circle.center.x, circle.center.x, 0)
    GL11.glTranslated(0, 0, 0)
    GL11.glBegin(GL11.GL_LINE_LOOP)

    for( i ← 0 until numSegments ) {
      val tetha = 2.0 * Math.PI * i.toDouble / numSegments.toDouble
      val point = Vector2D.new_rt(circle.radius, tetha)
      GL11.glVertex2f(point.x.toFloat, point.y.toFloat)
    }

    GL11.glEnd()
  }


}
