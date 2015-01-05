package com.colofabrix.scala

import java.io.{File, PrintWriter}

import com.colofabrix.scala.tankwar.World

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ): Unit = {
    val writer = new PrintWriter(new File("""out/tank.csv"""))

    val world = new World()

    (1 to 500) foreach { _ =>
      world.step()
    }

    writer.close()
  }
}
