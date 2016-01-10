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

package com.colofabrix.scala.gfx.ui

import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.gfx.ui.input.{ BooleanToggleKListener, KeyboardManager, NumUpdateKListener }
import com.colofabrix.scala.simulation.World
import org.lwjgl.Sys
import org.lwjgl.input.Keyboard

import scala.collection._

/**
 * Manages all of the user interfaces, such as Input and the GUI.
 *
 * @param world A reference to the World
 */
class UIManager( val world: World ) {

  private var framesThisSecond = 0
  private var lastFPS = ( Sys.getTime * 1000 ) / Sys.getTimerResolution
  /**
   * The keyboard manager
   */
  val KBM = new KeyboardManager()

  initializeListeners()

  /**
   * The flags of the user interface
   */
  val flags = new mutable.HashMap[String, Any] {
    def getWithDefault[T]( key: String, default: T ): T = {
      if ( !contains( key ) ) return default
      apply( key ) match {
        case v: T @unchecked ⇒ v
        case _ ⇒ default
      }
    }
  }

  initializeFlags()

  private def initializeFlags(): Unit = {
    flags += ( ( "sync" → 25 ) ) // Frame sync
    flags += ( ( "fps" → 0 ) )
    flags += ( ( "render" → false ) )
    flags += ( ( "pause" → false ) )
    flags += ( ( "vectors" → true ) )
    flags += ( ( "tksight" → true ) )
    flags += ( ( "tkinfo" → true ) )
    flags += ( ( "details" → true ) )

    return
  }

  private def initializeListeners(): Unit = {
    import Math._

    // Key "=": Increases max FPS
    KBM.addListener(
      new NumUpdateKListener[Int]( Keyboard.KEY_EQUALS, this, "sync", x ⇒ max( x + 5, 0 ) )
    )

    // Key "-": Increases max FPS
    KBM.addListener(
      new NumUpdateKListener[Int]( Keyboard.KEY_MINUS, this, "sync", x ⇒ max( x - 5, 0 ) )
    )

    // Key "v": Toggle the rendering of vectors
    KBM.addListener(
      new BooleanToggleKListener( Keyboard.KEY_V, this, "vectors" )
    )

    // Key "s": Toggle the rendering of the sight
    KBM.addListener(
      new BooleanToggleKListener( Keyboard.KEY_S, this, "tksight" )
    )

    // Key "i": Toggle the display of tank's information
    KBM.addListener(
      new BooleanToggleKListener( Keyboard.KEY_I, this, "tkinfo" )
    )

    // Key "d": Toggle the display of world's details
    KBM.addListener(
      new BooleanToggleKListener( Keyboard.KEY_D, this, "details" )
    )

    // Key "r": Toggle the rendering of the graphics
    KBM.addListener(
      new BooleanToggleKListener( Keyboard.KEY_R, this, "render" )
    )

    // Key "p": Toggle the pause of the simulation
    KBM.addListener(
      new BooleanToggleKListener( Keyboard.KEY_P, this, "pause" )
    )
  }

  /**
   * The renderers for the User Interface
   *
   * @return Returns the renderers for the User Interface
   */
  def renderers: Seq[Renderer] = Seq.empty

  /**
   * Update all of the UI including the KeyBoardManager
   */
  def update(): Unit = {
    KBM.update()
  }

}