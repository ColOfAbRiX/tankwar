package com.vogon101.Graphics.Controls

import com.colofabrix.scala.geometry.shapes.{Circle, Box}
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.tankwar.integration.TankEvaluator
import org.lwjgl.opengl.GL11
import org.newdawn.slick.opengl.Texture

/**
 * Created by Freddie on 18/05/2015.
 *
 *
 */
class ButtonControl (_screenArea: Box, action : (Int) => Unit) {

  require(_screenArea != null, "Must have a screen area")
  require(action != null, "Must have an action")

  private var _texture: Texture = null;

  def area = _screenArea
  def texture = _texture
  def texture_= (value: Texture): Unit = _texture = value




  def isInArea(x: Int, y: Int): Boolean = {
    if (x > area.bottomLeft.x && x < area.topRight.x)
      if (y > area.bottomLeft.y && y < area.topRight.y)
        return true
    return false
  }

  def runClick (mouseX: Int, mouseY: Int, mouseButton: Int){

    if (isInArea(mouseX, mouseY))
      action(mouseButton);

  }

  def render () {

    if (texture != null)
      throw new NotImplementedError("Not yet...")

    val xSize: Double = area.topRight.x - area.bottomLeft.x
    val ySize: Double = area.topRight.y - area.bottomLeft.y

    GL11.glPushMatrix()
      GL11.glTranslated(area.bottomLeft.x, area.bottomLeft.y, 0)
      GL11.glColor3d(1,0,0.5)
      GL11.glBegin(GL11.GL_QUADS)

        GL11.glVertex2d(0, 0)
        GL11.glVertex2d(0, ySize)
        GL11.glVertex2d(xSize, ySize)
        GL11.glVertex2d(xSize, 0)

      GL11.glEnd()

    GL11.glPopMatrix()

    println("Render")

  }

  //TODO: Render not working



}
