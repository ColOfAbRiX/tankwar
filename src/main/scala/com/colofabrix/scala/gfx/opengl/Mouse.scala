/*
 * Copyright (C) 2017 Fabrizio Colonna
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

package com.colofabrix.scala.gfx.opengl

import scalaz.{ -\/, \/, \/- }
import com.colofabrix.scala.math.{ Vect, XYVect }
import org.lwjgl.input.{ Mouse => GLM }

/**
  * Representation of the Mouse
  */
object Mouse {

  /**
    * Handler of keyboard events
    */
  trait MouseHandler {
    def onButtonPress(button: Int): Unit

    def onButtonRelease(button: Int): Unit
  }

  /** Handle the events occurred on the mouse. */
  def handle(handler: MouseHandler): Unit = {
    while (GLM.next()) {
      if (GLM.getEventButtonState)
        handler.onButtonPress(GLM.getEventButton)

      else
        handler.onButtonRelease(GLM.getEventButton)
    }
  }

  /** Return the position of the pointer. */
  def pointer(): Vect = XYVect(GLM.getX(), GLM.getY())

  /** Returns information about the mouse wheel. */
  def wheel(): Option[Int] = if (GLM.getDWheel != 0) Some(GLM.getDWheel) else None

  /** Return  information about the mouse buttons. */
  def buttons(): Option[\/[Vect, Vect]] = {
    if (GLM.isButtonDown(0))
      Some(-\/(this.pointer()))

    else if (GLM.isButtonDown(1))
      Some(\/-(this.pointer()))

    else
      None
  }
}
