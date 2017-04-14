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

import com.colofabrix.scala.math.{ Vect, XYVect }
import org.lwjgl.input.{ Mouse => GLM }

/**
  * Representation of the Mouse
  */
object Mouse {

  trait BtnAction

  final case class BtnPressed(button: Int, position: Vect) extends BtnAction

  final case class BtnReleased(button: Int, position: Vect) extends BtnAction

  final case class State(cursor: Vect, btnEvents: Seq[BtnAction], wheel: Int)

  /** Return the position of the pointer. */
  def cursor(): Vect = XYVect(GLM.getX(), GLM.getY())

  /** Return  information about the mouse buttons. */
  def state(): State = State(cursor(), events(), GLM.getDWheel)

  /** Gets all the events occured since the last execution. */
  def events(): Seq[BtnAction] = {
    if( GLM.next() )
      if( GLM.getEventButtonState ) {
        BtnPressed(GLM.getEventButton, cursor()) +: events()
      }
      else {
        BtnReleased(GLM.getEventButton, cursor()) +: events()
      }
    else Nil
  }
}