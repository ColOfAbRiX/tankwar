package com.colofabrix.scala

import com.colofabrix.scala.tankwar.tank.Tank

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ): Unit = {
    val t = new Tank()

    (1 to 1000) foreach { time =>
      println( t )
      t.stepForward()
    }
  }
}
