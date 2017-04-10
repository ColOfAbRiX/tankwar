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
import com.colofabrix.scala.gfx.opengl._
import com.colofabrix.scala.tankwar.simulation.World

/**
  *
  */
object Main {

  def main(args: Array[String]): Unit = {
    val renderState: Frame = OpenGL.init(1000, 800, "Tankwar V2")

    @tailrec
    def run(w: Option[World]): Unit = w match {
      case Some(x) ⇒
        OpenGL.applyContext(renderState) {
          for (t ← x.tanks) {
            Drawers.drawCircle(t.shape)
          }
        }
        val delta = Sync.sync(20).run(Sync.init())._2
        OpenGL.clearUp()

        run(x.step())

      case _ ⇒
    }

    run(Some(World()))
  }

}
