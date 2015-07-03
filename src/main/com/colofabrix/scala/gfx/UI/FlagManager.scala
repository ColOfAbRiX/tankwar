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

package com.colofabrix.scala.gfx.UI

import scala.collection.Map

/**
 * Class to contain UI and GFX flags that allow the Input to interact with the Simulation, GUI and GFX
 */
class FlagManager {

  //The map of the flags
  private var flags = Map[String, Any]( )

  /**
   * Get a flag from the map
   * @param name - The name of the flag to get
   * @param default - The default value to return if the flag is not found  = null
   * @param create - If true and the flag doesn't exist it will be created as the default value
   * @return
   */
  def getFlag( name: String, default: Any = null, create: Boolean = false ): Any = {
    if( flags.contains( name ) ) {
      return flags.get( name ).get
    }
    else {
      if( create ) {
        addFlag( name, default )
      }
      return default
    }
  }

  /**
   * Add a flag to the map
   * @param name - The name of the flag
   * @param item - The value of the flag to set it to
   */
  def addFlag( name: String, item: Any ): Unit = {
    flags += ((name, item))
  }

  /**
   * Update a flag in the Map
   * @param name - The name of the flag
   * @param item - The value to set it to
   */
  def updateFlag( name: String, item: Any ): Unit = {
    flags += ((name, item))
  }

  /**
   * Toggle a flag that is a boolean
   * @param name - The name of the flag
   * @param default - The value to set it to if it doesn't exist
   */
  def toggleBoolFlag( name: String, default: Boolean = true ): Unit = {
    if( getFlag( name ) != null && getFlag( name ).isInstanceOf[Boolean] ) {
      updateFlag( name, !getFlag( name ).asInstanceOf[Boolean] )
    }
    else {
      addFlag( name, default )
    }
  }


}
