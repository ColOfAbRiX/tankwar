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

package com.colofabrix.scala.tankwar

import scala.annotation.tailrec
import com.colofabrix.scala.gfx.opengl.Sync.SyncState
import com.colofabrix.scala.gfx.opengl._
import com.colofabrix.scala.tankwar.simulation.World
import org.lwjgl.opengl.Display

/**
  * Main Application
  */
object Main {

  @tailrec
  def run(ow: Option[World]): Unit = ow match {
    case Some(w) => run(w.step())
    case _ =>
  }

  @tailrec
  def run_gfx(ow: Option[World], state: SyncState): Unit = ow match {
    case Some(w) => if (!Display.isCloseRequested) {
      OpenGL.clear()

      for (t <- w.tanks) {
        Drawers.drawCircle(t.shape.center, t.shape.radius)
      }

      OpenGL.update()
      val (nextState, timeDelta) = Sync.sync(30).run(state)
      run_gfx(w.step(), nextState)
    }

    case _ =>
  }

  def main(args: Array[String]): Unit = {
    if (Configuration.Simulation.gxfEnabled) {
      OpenGL.init(800, 600)
      run_gfx(Some(World()), Sync.init())
      OpenGL.destroy()
    }
    else {
      run(Some(World()))
    }
  }
}