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
 *
 */
class InputManager {

  private val _buttons: ArrayBuffer[ButtonControl] = new ArrayBuffer[ButtonControl]()
  def buttons = _buttons
  init()

  def init (): Unit = {
    buttons.append(new ButtonControl( new Box( new Vector2D( new CartesianCoord(100, 100) ) , new Vector2D(new CartesianCoord(130, 130)) ) , (mb: Int) => {println("hi")} ))
  }

  def update() {
    if (!Mouse.isButtonDown(0))
      return

    val mouseX:Int = Mouse.getX
    val mouseY:Int = Mouse.getY

    for (button: ButtonControl <- buttons){
      button.runClick(mouseX, mouseY, 0)
    }
  }

}
