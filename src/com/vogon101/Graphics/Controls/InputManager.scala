package com.vogon101.Graphics.Controls

import java.util

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.math.{CartesianCoord, Vector2D}
import org.lwjgl.input.Mouse

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * Created by Freddie on 19/05/2015.
 *
 * This class contains all the buttons for controlling the game
 * Has an ArrayBuffer of ButtonControl to contain the buttons, the
 * update() method should be called once per step to check for any
 * clicks. If any clicks are found within a button, that button's
 * action will be called
 */
class InputManager {

  private val _buttons: ArrayBuffer[ButtonControl] = new ArrayBuffer[ButtonControl]()

  /**
   * @return The ArrayBuffer of ButtonControls
   */
  def buttons = _buttons
  init()

  private def init (): Unit = {
    buttons.append(new ButtonControl( new Box( new Vector2D( new CartesianCoord(100, 100) ) , new Vector2D(new CartesianCoord(130, 130)) ) , (mb: Int) => {println("hi")} ))
  }

  /**
   * Check for mouse clicks in any button an run the appropriate action if found
   */
  def update() {


    val mouseX:Int = Mouse.getX
    val mouseY:Int = Mouse.getY

    for (button: ButtonControl <- buttons){
      button.clickTimerInc()
      if (Mouse.isButtonDown(0))
        button.runClick(mouseX, mouseY, 0)
    }
  }

}
