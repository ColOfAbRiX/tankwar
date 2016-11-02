/*
 * Copyright (C) 2016 Fabrizio
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

import breeze.linalg.DenseVector
import com.typesafe.config.ConfigFactory

object Configuration {
  private val conf = ConfigFactory.load()

  object Global {
    object Arena {
      val size = DenseVector( Seq(
        conf.getInt( "world.arena.width" ),
        conf.getInt( "world.arena.height" )
      ) )
    }

    val arena = Arena
    val tanks = conf.getInt( "world.tankCount" )
    val rounds = conf.getInt( "world.rounds" )
  }

}
