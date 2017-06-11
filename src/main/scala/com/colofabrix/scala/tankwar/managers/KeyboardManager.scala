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

import scalaz.State
import com.colofabrix.scala.physix.shapes.Box
import com.colofabrix.scala.gfx.Keyboard
import com.colofabrix.scala.gfx.Keyboard._
import com.colofabrix.scala.math.XYVect
import com.colofabrix.scala.tankwar.Configuration.{ Graphics => GfxConfig, World => WorldConfig }
import com.colofabrix.scala.tankwar.SimState
import com.typesafe.scalalogging.LazyLogging
import org.lwjgl.input.Keyboard._

/**
  * Manages keyboard actions for the game
  */
object KeyboardManager extends Manager[SimState] with LazyLogging {

  def apply(): ManagerAction = State { outerState =>
    //
    // Manage keys that perform actions when pressed continuosly
    //
    val continuousActions = for {
      // Simulation speed
      _ <- OnKeyDown(KEY_ADD) { state: SimState =>
        logger.info(s"KEY_ADD pressed: increase simulation speed.")
        val td = state.world.timeDelta * (1.0 + 1.0 / GfxConfig.fps)
        state.copy(world = state.world.copy(timeDelta = td))
      }
      _ <- OnKeyDown(KEY_SUBTRACT) { state: SimState =>
        logger.info(s"KEY_SUBTRACT pressed: decrease simulation speed.")
        val td = state.world.timeDelta * (1.0 - 1.0 / GfxConfig.fps)
        state.copy(world = state.world.copy(timeDelta = td))
      }

      // Scroll viewport
      _ <- OnKeyDown(KEY_W) { state: SimState =>
        logger.info(s"KEY_W pressed: Move viewport up.")
        val vp = state.display.viewport.move(XYVect(0.0, 0.5 * state.display.viewport.height) / GfxConfig.fps)
        state.copy(display = state.display.copy(viewport = vp))
      }
      _ <- OnKeyDown(KEY_A) { state: SimState =>
        logger.info(s"KEY_A pressed: Move viewport left.")
        val vp = state.display.viewport.move(XYVect(-0.5 * state.display.viewport.width, 0.0) / GfxConfig.fps)
        state.copy(display = state.display.copy(viewport = vp))
      }
      _ <- OnKeyDown(KEY_S) { state: SimState =>
        logger.info(s"KEY_S pressed: Move viewport down.")
        val vp = state.display.viewport.move(XYVect(0.0, -0.5 * state.display.viewport.height) / GfxConfig.fps)
        state.copy(display = state.display.copy(viewport = vp))
      }
      _ <- OnKeyDown(KEY_D) { state: SimState =>
        logger.info(s"KEY_D pressed: Move viewport right.")
        val vp = state.display.viewport.move(XYVect(0.5 * state.display.viewport.width, 0.0) / GfxConfig.fps)
        state.copy(display = state.display.copy(viewport = vp))
      }

      // Zoom viewport
      _ <- OnKeyDown(KEY_Q) { state: SimState =>
        logger.info(s"KEY_Q pressed: Zoom viewport in.")
        val vp = state.display.viewport.scale(1.0 + 1.0 / GfxConfig.fps)
        state.copy(display = state.display.copy(viewport = vp))
      }
      s <- OnKeyDown(KEY_E) { state: SimState =>
        logger.info(s"KEY_E pressed: Zoom viewport out.")
        val vp = state.display.viewport.scale(1.0 - 1.0 / GfxConfig.fps)
        state.copy(display = state.display.copy(viewport = vp))
      }
    } yield s

    val state1 = continuousActions.run(outerState)

    //
    // Managing keys that perform actions only when changing state
    //
    val state2 = Keyboard.events().foldLeft(state1._1) {
      case (state, Keyboard.KeyPressed(k)) =>
        if (k == KEY_H) {
          logger.info("KEY_H pressed: Reset viewport.")
          state.copy(display = state.display.copy(viewport = Box(WorldConfig.Arena.width, WorldConfig.Arena.height)))
        }

        else if (k == KEY_P) {
          logger.info(s"KEY_P pressed: Toggle pause.")
          state.copy(pause = !outerState.pause)
        }

        else if (k == KEY_F && (Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT))) {
          logger.info(s"KEY_F + KEY_xSHIFT pressed: Toggle view of force fields.")
          state.copy(display = state.display.copy(forceField = !outerState.display.forceField))
        }

        else if (k == KEY_V && (Keyboard.isKeyDown(KEY_LSHIFT) || Keyboard.isKeyDown(KEY_RSHIFT))) {
          logger.info(s"KEY_V + KEY_xSHIFT pressed: Toggle view of vectors.")
          state.copy(display = state.display.copy(vectors = !outerState.display.vectors))
        }

        else state

      case (s, _) => s
    }

    ret(state2)
  }

}
