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

package com.colofabrix.scala.tankwar.managers

import com.colofabrix.scala.geometry.shapes.Box
import com.colofabrix.scala.gfx.Keyboard
import com.colofabrix.scala.gfx.Keyboard._
import com.colofabrix.scala.math.XYVect
import com.colofabrix.scala.tankwar.Configuration.{ Simulation => SimConfig, World => WorldConfig }
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.input.Keyboard._

/**
  * Manages keyboard actions for the game
  */
object KeyboardManager extends SimManager[GraphicSimulation] with LazyLogging {

  type TWKeyAction = KeyStateAction[GraphicSimulation]

  def manage(): SimAction = scalaz.State { state =>
    //
    // Manage keys that perform actions when pressed continuosly
    //
    val actions1 = Seq(
      new TWKeyAction(KEY_ADD)(
        s => {
          logger.info(s"KEY_ADD pressed: increase simulation speed.")
          s.copy(tsMultiplier = Math.min(50.0, s.tsMultiplier * (1.0 + 1.0 / SimConfig.fps)))
        }
      ),
      new TWKeyAction(KEY_SUBTRACT)(
        s => {
          logger.info(s"KEY_SUBTRACT pressed: decrease simulation speed.")
          s.copy(tsMultiplier = Math.max(0.05, s.tsMultiplier * (1.0 - 1.0 / SimConfig.fps)))
        }
      ),

      new TWKeyAction(KEY_W)(
        s => {
          logger.info(s"KEY_W pressed: Move viewport up.")
          s.copy(viewport = s.viewport.moveOf(XYVect(0.0, 0.5 * s.viewport.height) / SimConfig.fps))
        }
      ),
      new TWKeyAction(KEY_A)(
        s => {
          logger.info(s"KEY_A pressed: Move viewport left.")
          s.copy(viewport = s.viewport.moveOf(XYVect(-0.5 * s.viewport.width, 0.0) / SimConfig.fps))
        }
      ),
      new TWKeyAction(KEY_S)(
        s => {
          logger.info(s"KEY_S pressed: Move viewport down.")
          s.copy(viewport = s.viewport.moveOf(XYVect(0.0, -0.5 * s.viewport.height) / SimConfig.fps))
        }
      ),
      new TWKeyAction(KEY_D)(
        s => {
          logger.info(s"KEY_D pressed: Move viewport right.")
          s.copy(viewport = s.viewport.moveOf(XYVect(0.5 * s.viewport.width, 0.0) / SimConfig.fps))
        }
      ),

      new TWKeyAction(KEY_Q)(
        s => {
          logger.info(s"KEY_Q pressed: Zoom viewport in.")
          s.copy(viewport = s.viewport.scale(1.0 + 1.0 / SimConfig.fps))
        }
      ),
      new TWKeyAction(KEY_E)(
        s => {
          logger.info(s"KEY_E pressed: Zoom viewport out.")
          s.copy(viewport = s.viewport.scale(1.0 - 1.0 / SimConfig.fps))
        }
      )
    )

    val state1 = actions1.foldLeft(state) { (s, action) => action.runWhenDown(s) }

    //
    // Managing keys that perform actions only when changing state
    //
    val state2 = Keyboard.events().foldLeft(state1) {
      case (s, Keyboard.KeyDown(k, true)) =>
        if( k == KEY_H ) {
          logger.info("KEY_H pressed: Reset viewport.")
          s.copy(viewport = Box(WorldConfig.Arena.width, WorldConfig.Arena.height))
        }

        else if( k == KEY_P ) {
          logger.info(s"KEY_P pressed: Toggle pause.")
          s.copy(pause = !state.pause)
        }

        else if( k == KEY_F && (Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT)) ) {
          logger.info(s"KEY_F + KEY_xSHIFT pressed: Toggle view of force fields.")
          s.copy(display = s.display.copy(forceField = !state.display.forceField))
        }

        else if( k == KEY_V && (Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT)) ) {
          logger.info(s"KEY_V + KEY_xSHIFT pressed: Toggle view of vectors.")
          s.copy(display = s.display.copy(vectors = !state.display.vectors))
        }

        else s

      case (s, _) => s
    }

    returnState(state2)
  }

}
