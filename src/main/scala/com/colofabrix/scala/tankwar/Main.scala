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
import com.colofabrix.scala.gfx.opengl.OpenGL._
import com.colofabrix.scala.gfx.opengl._
import com.colofabrix.scala.tankwar.simulation.World
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.GL11._

/**
  *
  */
object Main {

  @tailrec
  def run(ow: Option[World]): Unit = ow match {
    case Some(w) => if( !Display.isCloseRequested ) {
      OpenGL.clear()

      for( t <- w.tanks ) {
        Drawers.drawCircle(t.shape)
      }

      OpenGL.update()
      Sync.sync(30).run(Sync.init())._2

      run(w.step())
    }

    case _ =>
  }

  def main(args: Array[String]): Unit = {
    OpenGL.init(800, 600)
    run(Some(World()))
    OpenGL.destroy()
  }
}