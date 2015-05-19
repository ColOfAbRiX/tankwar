package com.vogon101.Graphics

import com.colofabrix.scala.geometry.shapes.{Box, Circle}
import com.colofabrix.scala.math.{CartesianCoord, Vector2D}
import com.colofabrix.scala.tankwar.Bullet
import com.colofabrix.scala.tankwar.Tank
import com.colofabrix.scala.tankwar.World
import com.colofabrix.scala.tankwar.integration.TankEvaluator
import com.vogon101.Graphics.Controls.ButtonControl
import org.lwjgl.LWJGLException
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode
import org.lwjgl.opengl.GL11
import scala.collection.mutable.ListBuffer

/**
 *
 * Created by Freddie on 17/05/2015.
 *
 * This class is used to render the game world, it has only one
 * public method which is to run the entire render process
 *
 *@param _world - The world object that the class should work from to get the tanks/bullets to render
 *
 */
class Renderer (private var _world: World){

  /*
  This class is being refactored into a new system of graphics that aren't all in one file.
   */


  /**
   * Return the current world type
   * @return - The world object
   */
  def world = _world

  private val width = world.arena.width
  private val height = world.arena.height

  private val testButton = new ButtonControl(new Box(new Vector2D(new CartesianCoord(100,100)), new Vector2D(new CartesianCoord(200,200))), (mouse: Int) => {println(mouse)})

  init()

  private def init () {
    try {
      initGL()
    }
    catch  {
      case e : LWJGLException => {println("Could not start Graphics"); System exit 1}
    }



    setCamera()
  }

  /**
   * Main update function calls the entire render process
   * Should be called once per tick
   */
  def update() {
    setCamera()
    drawBG()
    drawTanks()
    drawBullets()
    drawGUI();
    Display.sync(25)
    Display.update()



    if (Display.isCloseRequested)
      System.exit(0)
  }

  def drawGUI (): Unit = {

    for (button: ButtonControl <- world.inputManager.buttons)
      button.render()

  }


  @throws(classOf[LWJGLException])
  private def initGL() {
    Display.setDisplayMode(new DisplayMode(width.toInt, height.toInt))
    Display.create()
    Display setTitle "TankWar"
  }

  private def setCamera() {
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

  private def drawTanks() {
    val tanks: ListBuffer[Tank] = world.tanks
    for ( tank: Tank <- tanks) {
      if ( !tank.isDead )
        drawTank(tank)
    }
  }

  private def drawTank(tank: Tank) {
    val size: Double = tank.boundary.asInstanceOf[Circle].radius
    val fitness: Double = new TankEvaluator().getFitness(tank, null)
    GL11.glPushMatrix()
      GL11.glTranslated(tank.position.x, tank.position.y, 0)
      GL11.glRotated(tank.rotation.t * 180 / Math.PI, 0, 0, 1)
      GL11.glColor3d(1, 2 * fitness / TankEvaluator.higherFitness(world), 0)
      GL11.glBegin(GL11.GL_TRIANGLES)

        GL11.glVertex2d(size, 0.0)
        GL11.glVertex2d(-0.866025 * size, 0.5 * size)
        GL11.glVertex2d(-0.866025 * size, -0.5 * size)

      GL11.glEnd()
      drawCircle(new Circle(Vector2D.origin, size))
      GL11.glColor3d(0.1, 0.1, 0.1)
      val sight: Double = world.max_sight
      drawCircle(new Circle(Vector2D.origin, sight))

    GL11.glPopMatrix()
  }

  private def drawBullets() {
    val bullets: ListBuffer[Bullet] = world.bullets.clone()
    for (bullet: Bullet <- bullets)
      drawBullet( bullet )
  }

  private def drawBullet(bullet: Bullet) {
    val size: Double = bullet.boundary.asInstanceOf[Circle].radius
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

  private def drawBG() {
    GL11.glPushMatrix ()
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
  //
  /**
   * Draws an approximate Circle Circle
   *
   * Draws a circle as a regular polygon with a specific number of edges
   * Ref: http://slabode.exofire.net/circle_draw.shtml
   *
   * @param circle The circle to draw
   */
  private def drawCircle(circle: Circle) {
    val numSegments: Int = (circle.radius * 2.0 * Math.PI).toInt
    GL11.glBegin(GL11.GL_LINE_LOOP)
      var i: Int = 0
      while (i < numSegments) {
          val theta: Double = 2.0 * Math.PI * i.toDouble / numSegments.toDouble
          val x: Double = circle.radius * Math.cos(theta)
          val y: Double = circle.radius * Math.sin(theta)
          GL11.glVertex2f((x + circle.center.x).toFloat, (y + circle.center.y).toFloat)
          i += 1; i - 1
      }
    GL11.glEnd()
  }


}
