package com.colofabrix.scala

import com.colofabrix.scala.tankwar.World

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ): Unit = {
    val world = new World()
    (1 to 500) foreach { _ => world.step() }
  }
}
