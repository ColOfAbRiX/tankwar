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

package com.colofabrix.scala.gfx.ui.input

import org.lwjgl.input.Keyboard

/**
 * Class to manage all keyboard input, has an array buffer of KeyboardListeners which can listen for keyboard events
 */
class KeyboardManager {

  /**
   * The ArrayBuffer of listeners
   */
  @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Var" ) )
  var keyboardListeners = Seq.empty[KeyboardListener]

  /**
   * Add a listener to the list
   *
   * @param listener The Listener to add
   */
  def addListener( listener: KeyboardListener ): Unit = {
    keyboardListeners = listener +: keyboardListeners
  }

  /**
   * Update the keyboard ie poll for input. Called once per frame by the Game
   */
  def update(): Unit = {

    while ( Keyboard.next() ) {
      keyboardListeners.filter( _.eventDriven ).foreach {
        _.checkEvent( Keyboard.getEventKey, Keyboard.getEventKeyState )
      }
    }

    keyboardListeners.filter( !_.eventDriven ).foreach( _.update() )

  }

}