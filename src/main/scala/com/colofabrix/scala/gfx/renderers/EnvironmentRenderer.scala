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

import com.colofabrix.scala.gfx.OpenGL._
import com.colofabrix.scala.gfx.abstracts.Renderer
import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.simulation.World
import scala.language.reflectiveCalls

/**
  *
  */
class EnvironmentRenderer( val world: World ) extends Renderer {

  import com.colofabrix.scala.gfx.renderers.TextRenderer._

  override def render( create: Boolean ): Unit = {

    @SuppressWarnings( Array( "org.brianmckenna.wartremover.warts.Nothing" ) )
    val flags = world.UIManager.flags

    if ( flags.getWithDefault( "details", true ) ) {

      applyContext( Frame( Colour.CYAN, Vector2D.new_xy( 10, 10 ) ) ) {

        List(
          "Max Simulation Speed: " + flags.getWithDefault( "sync", 25 ) + "fps",
          "Number Of Hits: " + world.counters.getOrElse( "hits", 0 ).toString,
          "Number Of Shots: " + world.counters.getOrElse( "shots", 0 ).toString,
          "Hit Percentage: " + ( world.counters.getOrElse( "hits", 0 ).toFloat / ( world.counters.getOrElse( "shots", 0 ).toFloat + 1.0 ) ).toString,
          "Generation: -",
          "Best tank fitness: -",
          "Average fitness: -"
        ).renderer.render( false )

      }

    }
  }

}