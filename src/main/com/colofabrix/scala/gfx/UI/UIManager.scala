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

import com.colofabrix.scala.gfx.Renderer
import com.colofabrix.scala.gfx.UI.Input.{KeyboardListener, KeyboardManager}
import com.colofabrix.scala.simulation.World
import org.lwjgl.input.Keyboard

import scala.collection.mutable.Map


/**
 */
class UIManager (val world: World){

  val KBM = new KeyboardManager()

  //This is an example keyboard listener that toggles a variable in the Flags HashMap
  //KBM.addListener(new KeyboardListener(Keyboard.KEY_A, () => {Flags.toggleBoolFlag("A")}, true))

  //TODO: UI RENDERERS
  def getRenderers(): Array[Renderer] ={
    return null
  }

  def UpdateUI (): Unit = {
    KBM.update()
    println(Flags.getFlag("A"))
  }

}

object Flags {

  private val flags:Map[String, Any] = Map()

  def getFlag(name: String): Any = {
    if (flags.contains(name)){
      return flags.get(name).get
    }
    return null
  }

  def addFlag (name: String, item: Any): Unit = {
    flags.put(name, item)
  }

  def updateFlag (name: String, item: Any): Unit = {
    flags(name) = item
  }

  def toggleBoolFlag (name: String, default: Boolean = true): Unit = {
    if (getFlag(name) != null) {
      updateFlag(name, !getFlag(name).asInstanceOf[Boolean])
    }
    else {
      addFlag(name, default)
    }
  }

}
