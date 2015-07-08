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

import com.colofabrix.scala.simulation.World
import org.lwjgl.input.Keyboard

import scala.collection.mutable

/**
 *
 * Class to listen for keyboard input, can be event driven, ie activated on a discrete press
 * or release or continuous, ie activated each frame when the key is down. If event driven
 * then it can be onPress or onRelease
 *
 * @param key The key to listen for, a static integer found in the LWJGL Keyboard class
 * @param action The code to run when activated
 * @param eventDriven If true then will only be activated on a discrete press/release otherwise it is every frame while the key is down
 * @param onPress If eventDriven then this determined if it should be activated onPress (T) or onRelease (F)
 */
class KeyboardListener( val key: Int,
  val action: (World) => Unit,
  val world: World,
  val eventDriven: Boolean = true,
  val onPress: Boolean = true) {

  /**
   * Update the key if it is not eventDriven
   */
  def update( ): Unit = {
    if( !eventDriven && Keyboard.isKeyDown( key ) ) {
      action( world )
    }
  }

  /**
   * Check the key on an event if it is event driven
   *
   * @param eKey The key integer of the event
   * @param isPress True if the event is a press, false if it is a release
   */
  def checkEvent( eKey: Int, isPress: Boolean ): Unit = {
    // If this listener is event driven and not continuous polling and is the right type (release/press) and it is the right key
    if( eventDriven && isPress == onPress && eKey == key ) {
      action( world )
    }
  }

}