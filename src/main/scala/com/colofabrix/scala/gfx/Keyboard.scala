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

package com.colofabrix.scala.gfx

import org.lwjgl.input.{ Keyboard => GLK }

/**
  * Representation of the Keyboard
  */
object Keyboard {

  trait KeyAction

  final case class KeyPressed(key: Int) extends KeyAction

  final case class KeyReleased(key: Int) extends KeyAction

  final case class State(keyEvents: Seq[KeyAction])

  /** Return  information about the mouse buttons. */
  def state(): State = State(events())

  /** Gets all the events occured since the last execution. */
  def events(): Seq[KeyAction] = {
    if( GLK.next() )
      if( GLK.getEventKeyState ) {
        KeyPressed(GLK.getEventKey) +: events()
      }
      else {
        KeyReleased(GLK.getEventKey) +: events()
      }
    else Nil
  }

}