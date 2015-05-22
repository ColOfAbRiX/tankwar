package com.colofabrix.scala.gfx.Controls

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.Vector2D
import org.lwjgl.opengl.GL11
import org.newdawn.slick.opengl.Texture

/**
 * Created by Freddie on 18/05/2015.
 *
 * This class contains a Button. It has a render method, so is responsible for its own rendering
 * as well as a Unit that it is it's OnClick action. Requires a Box as a renderable/clickable area and
 * a Unit as an action
 *
 */
class ButtonControl( area: Box, texture: Texture, action : (Int) => Unit) {

  //TODO: Make timing in seconds not steps
  // ->TODO: Add a universal DeltaTime for the project

  require(area != null, "Must have a screen area")
  require(action != null, "Must have an action")
  
  /**
   * The minimum amount of time between clicks
   */
  val MIN_CLICK_TIMER = 25
  private var clickTimer: Int = MIN_CLICK_TIMER

  /**
   * Increment the click timer by one
   */
  def clickTimerInc() {clickTimer += 1}

  /**
   *
   * Runs a click, tests if the click is within the clickable area, if so
   * runs the action
   *
   * @param mouse Position of the mouse
   * @param mouseButton Int - The mouse button number, passed to the action
   */
  def runClick( mouse: Vector2D, mouseButton: Int ) {
    if (clickTimer >= MIN_CLICK_TIMER) {

      if( area.overlaps(mouse) ) {
        action(mouseButton)
        clickTimer = 0
      }

    }
  }

  /**
   * Renders the button
   */
  def render() {

    if (texture != null)
      throw new NotImplementedError("Not yet...")

    GL11.glPushMatrix()

      GL11.glTranslated(area.bottomLeft.x, area.bottomLeft.y, 0)
      GL11.glColor3d(1,0,0.5)

      GL11.glBegin(GL11.GL_QUADS)

        GL11.glVertex2d(0, 0)
        GL11.glVertex2d(0, area.height)
        GL11.glVertex2d(area.width, area.height)
        GL11.glVertex2d(area.width, 0)

      GL11.glEnd()

    GL11.glPopMatrix()

  }

  // TODO: Textures for button

}
