package com.colofabrix.scala

import java.io.{File, PrintWriter}

import com.colofabrix.scala.tankwar.Tank

/**
 * Main game class
 *
 * Created by Fabrizio on 29/09/2014.
 */
object TankWarMain {
  def main( args: Array[String] ): Unit = {
    val writer1 = new PrintWriter(new File("""out/tank.csv"""))
    //val writer2 = new PrintWriter(new File("""tank2.csv"""))

    val t1 = new Tank()
    println( t1.brain.toString )
    //val t2 = new Tank()

    (1 to 500) foreach { time =>
      writer1.write( t1.toString + "\n" )
      //println( t1.toString )
      t1.stepForward()

      //writer2.write( t2.toString )
      //t2.stepForward()
    }

    writer1.close()
    //writer2.close()
  }
}
