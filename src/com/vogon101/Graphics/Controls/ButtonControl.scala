package com.vogon101.Graphics.Controls

import com.colofabrix.scala.geometry.shapes.{Circle, Box}
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.tankwar.integration.TankEvaluator
import org.lwjgl.opengl.GL11
import org.newdawn.slick.opengl.Texture

/**
 * Created by Freddie on 18/05/2015.
 *
 * This class contains a Button. It has a render method, so is responsable for its own rendering
 * as well as a Unit that it is it's OnClick action. Requires a Box as a renderable/clickable area and
 * a Unit as an action
 *
 */
class ButtonControl (_screenArea: Box, action : (Int) => Unit) {

  //TODO: Make timing in seconds not steps
  // ->TODO: Add a universal DeltaTime for the project

  require(_screenArea != null, "Must have a screen area")
  require(action != null, "Must have an action")


  /**
   * The minimum amount of time between clicks
   */
  val MIN_CLICK_TIMER = 25;
  private var clickTimer: Int = MIN_CLICK_TIMER;

  /**
   * Increment the click timer by one
   */
  def clickTimerInc() {clickTimer += 1}

  private var _texture: Texture = null;

  /**
   * @return The Box of the screen area
   */
  def area = _screenArea

  /**
   * @return The Texture of the box - UNIMPLEMENTED
   */
  def texture = _texture

  /**
   * Set the texture
   * @param value The Texture to set it to - UNIMPLEMENTED
   */
  def texture_= (value: Texture): Unit = _texture = value



  /**
   * Check if a point is in the button's clickable area
   *
   */
  def isInArea(x: Int, y: Int): Boolean = {
    if (x > area.bottomLeft.x && x < area.topRight.x)
      if (y > area.bottomLeft.y && y < area.topRight.y)
        return true
    return false
  }

  /**
   *
   * Runs a click, tests if the click is within the clickable area, if so
   * runs the action
   *
   * @param mouseX Int - The X position of the mouse
   * @param mouseY Int - The Y position of the mouse
   * @param mouseButton Int - The mouse button number, passed to the action
   */
  def runClick (mouseX: Int, mouseY: Int, mouseButton: Int) {

    if (!(clickTimer >= MIN_CLICK_TIMER)) {
      return
    }

    if (isInArea(mouseX, mouseY)) {
      action(mouseButton);
      clickTimer = 0
    }

  }

  /**
   * Renders the button
   */
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

  }

  //TODO: Textures for button



}
