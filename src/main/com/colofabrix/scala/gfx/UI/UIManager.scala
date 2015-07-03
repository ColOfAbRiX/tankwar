/*
 * Copyright (C) 2015 Freddie Poser
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

package com.colofabrix.scala.gfx.UI

import com.colofabrix.scala.gfx.Renderer
import com.colofabrix.scala.gfx.UI.Input.{ KeyboardListener, KeyboardManager }
import com.colofabrix.scala.simulation.World
import org.lwjgl.input.Keyboard

import scala.collection.mutable.Map


/**
 * A class that manages all of the UserInterface such as Input and the GUI
 */
class UIManager( val world: World ) {

  /**
   * The keyboard manager
   */
  val KBM = new KeyboardManager( )

  //This is an example keyboard listener that toggles a variable in the flags Map
  //KBM.addListener(new KeyboardListener( Keyboard.KEY_A, (world: World) => {world.UIManager.flags.toggleBoolFlag("A")}, world, true))

  /**
   * The flags [Wrapper for a Map]
   */
  val flags = new FlagManager( )

  //This is an example keyboard listener that toggles a variable in the Flags HashMap

  //TODO: UI RENDERERS
  /**
   * Get the renderers for the GUI
   * @return The Renderers
   */
  def getRenderers( ): Array[Renderer] = {
    return null
  }

  /**
   * Update all of the UI including the KeyBoardManager
   */
  def UpdateUI( ): Unit = {
    KBM.update( )
  }

}

