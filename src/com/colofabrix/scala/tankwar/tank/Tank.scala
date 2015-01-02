package com.colofabrix.scala.tankwar.tank

import com.colofabrix.scala.neuralnetwork.GenericNeuralNetwork
import com.colofabrix.scala.neuralnetwork.builders._

/**
 * A Tank in the Game
 *
 * Created by Fabrizio on 02/01/2015.
 */
class Tank {
  private var time: Long = 0
  private var position_x: Double = 0.0
  private var position_y: Double = 0.0
  private var rotation: Double = 0.0
  private val arena = (500, 500)

  val brain: GenericNeuralNetwork = new Random3LNetwork(4, 5, 3, 0.1).build

  private def calculate =
    brain.output(
      Seq(position_x, position_y, rotation, time.toDouble)
    )

  def stepForward() {
    time += 1

    val outputs = calculate

    // Updating values
    position_x += outputs(0) * 5
    position_y += outputs(1) * 5
    rotation += outputs(2) / 5

    // Conditioning
    //position_x = position_x % arena._1 / 2 + arena._1 / 2
    //position_y = position_y % arena._2 / 2 + arena._2 / 2
    //rotation %= 2 * Math.PI

    // Trimming
    position_x = Math.round(position_x)
    position_y = Math.round(position_y)
  }

  override def toString = s"$time;$position_x;$position_y;$rotation".replace(".", ",")
}
