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
import com.colofabrix.scala.gfx.ui.input.{ KeyboardListener, KeyboardManager }
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

  private var lastFPS = (Sys.getTime * 1000) / Sys.getTimerResolution
  private var framesThisSecond = 0

  /**
   * The keyboard manager
   */
  val KBM = new KeyboardManager( )

  initializeListeners( )

  /**
   * The flags of the user interface
   */
  val flags = new mutable.HashMap[String, Any] {
    def getWithDefault[T]( key: String, default: T ): T = {
      if( !contains( key ) ) return default
      apply( key ) match {
        case v: T => v
        case _ => default
      }
    }
  }

  initializeFlags( )

  private def initializeFlags( ): Unit = {
    flags += ("sync" -> 25) // Frame sync
    flags += ("fps" -> 0)
    flags += ("qtree" -> false)
    flags += ("vectors" -> true)
    flags += ("tksight" -> true)
    flags += ("details" -> true)
  }

  private def updateFPS( ): Unit = {
    val time = (Sys.getTime * 1000) / Sys.getTimerResolution
    var fps = flags.getWithDefault( "fps", 0 )
    if( time - lastFPS > 1000 ) {
      flags.update( "fps", framesThisSecond )
      lastFPS += 1000
      framesThisSecond = 0
    }
    framesThisSecond += 1
  }

  /**
   * The renderers for the User Interface
   *
   * @return Returns the renderers for the User Interface
   */
  def renderers: Seq[Renderer] = Seq( )

  /**
   * Update all of the UI including the KeyBoardManager
   */
  def update( ): Unit = {
    KBM.update( )
    updateFPS( )
  }

  private def initializeListeners( ): Unit = {
    //Add the right listeners
    //This listener adds 5 to the sync flag (controls sim. speed) when plus key pressed
    KBM.addListener(
      new KeyboardListener(
        Keyboard.KEY_EQUALS,
        ( world: World ) => {
          val flags = world.UIManager.flags
          flags.update( "sync", flags.getWithDefault( "sync", 25 ) + 5 )
        },
        world
      )
    )
    //This listener minuses 5 from the sync flag when the minus key is pressed (Min 5)
    KBM.addListener(
      new KeyboardListener(
        Keyboard.KEY_MINUS,
        ( world: World ) => {
          val flags = world.UIManager.flags
          val speed = flags.getWithDefault( "sync", 25 ) - 5
          if( speed > 4 ) flags.update( "sync", speed )
        },
        world
      )
    )
    //This flag toggles the qtree flag which controls the rendering of the QuadTree
    KBM.addListener(
      new KeyboardListener(
        Keyboard.KEY_Q,
        ( world: World ) => {
          val flags = world.UIManager.flags
          val qtree = flags.getWithDefault( "qtree", true )
          flags.update( "qtree", !qtree )
        },
        world
      )
    )
    //This listener toggles the vectors flag which controls the rendering of the tanks' vectors
    KBM.addListener(
      new KeyboardListener(
        Keyboard.KEY_V,
        ( world: World ) => {
          val flags = world.UIManager.flags
          val vectors = flags.getWithDefault( "vectors", true )
          flags.update( "vectors", !vectors )
        },
        world
      )
    )
    //This listener toggles the tksight flag which controls the rendering of the tank's sight
    KBM.addListener(
      new KeyboardListener(
        Keyboard.KEY_S,
        ( world: World ) => {
          val flags = world.UIManager.flags
          val tksight = flags.getWithDefault( "tksight", true )
          flags.update( "tksight", !tksight )
        },
        world
      )
    )

    //This listener toggles the details flag which controls the rendering of the data about the simulation
    KBM.addListener(
      new KeyboardListener(
        Keyboard.KEY_D,
        ( world: World ) => {
          val flags = world.UIManager.flags
          val details = flags.getWithDefault( "details", true )
          flags.update( "details", !details )
        },
        world
      )
    )
  }

}

