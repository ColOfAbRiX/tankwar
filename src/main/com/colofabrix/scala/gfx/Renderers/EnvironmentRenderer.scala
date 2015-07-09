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

package com.colofabrix.scala.gfx.renderers

import com.colofabrix.scala.geometry.LinkedQuadtree
import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.World
import com.colofabrix.scala.simulation.abstracts.PhysicalObject

/**

  */
class EnvironmentRenderer( val world: World, quadtree: => LinkedQuadtree[_ <: Shape, _ <: PhysicalObject] )
  extends Renderer {

  override def render( create: Boolean ): Unit = {
    val flags = world.UIManager.flags
    if( flags.getWithDefault( "qtree", true ) ) {
      quadtree.renderer.render( create = true )
    }

    if (flags.getWithDefault("details", true)) {
      val tr = new TextRenderer(
        List[String]("Max Simulation Speed: " + flags.getWithDefault( "sync", 25 ).toString + "fps",
          "Number Of Hits: " + world.counters.get( "hits" ).get.toString,
          "Number Of Shots: " + world.counters.get( "shots" ).get.toString,
          "Hit Percentage: " + (world.counters.get( "hits" ).get.toFloat/(world.counters.get( "shots" ).get.toFloat+1)).toString,
          "FPS: " + flags.getWithDefault("fps", 0).toString),
        Vector2D.new_xy( 10, 10 ) )
      tr.render( )
    }
  }

}
