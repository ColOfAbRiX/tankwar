package com.colofabrix.scala.tankwar

import java.io.PrintWriter

import com.colofabrix.scala.neuralnetworkOld.NeuralNetworkTester

/**
 * Provides a way to visualize the behaviour of a NN
 *
 * This class allows to create CSV files to analyse the various outputs varying
 * specific inputs.
 *
 * Created by Fabrizio on 15/02/2015.
 */
final class TankBrainTester(val tank: Tank, nInputs: Int)
extends NeuralNetworkTester(tank.brain, nInputs) {

  // Range of the values and number of points (input#, start_value, end_value, points_count)
  override val plotDefinitions = List(
    (0, 0.0, tank.world.arena.topRight.x, 250.0),
    (1, 0.0, tank.world.arena.topRight.y, 250.0),
    (2, 0.0, tank.world.max_tank_speed, 250.0),
    (3, 0.0, tank.world.max_tank_speed, 250.0),
    (4, 0.0, 2 * Math.PI, 250.0),
    (5, 0.0, tank.world.max_sight, 250.0),
    (6, 0.0, 2 * Math.PI, 250.0)
  )

  override val testDefinitions: List[(PrintWriter ⇒ Unit)] = List(
    //fullAnalysis(Seq(2500, 2500, 0, 0, 0, 0, 0), 5, 6)(_)
  )

  override protected def evaluateNetwork(): Unit = ???
}
