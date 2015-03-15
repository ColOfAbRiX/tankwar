package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.shapes.Polygon
import com.colofabrix.scala.neuralnetwork.abstracts.{InputHelper, OutputHelper}
import com.colofabrix.scala.neuralnetwork.builders.abstracts.StructureBuilder

/**
 * A TankChromosome contains all the data needed to uniquely identify
 * a Tank from another. Tank with identical chromosomes behave in the
 * same way
 */
case class TankChromosome(
  biases: Seq[Seq[Double]],
  weights: Seq[Seq[Seq[Double]]],
  sight: TankSight,
  mass: Double,
  valueRange: Double,
  activationFunction: Seq[String],
  brainBuilder: StructureBuilder
) {

  /**
   * Returns all the data as a sequence that can be used in loops and similar
   *
   * @return A sequence containing all the fields
   */
  def toList: List[Any] = List(
    biases, weights, sight, mass, valueRange, activationFunction, brainBuilder
  )
}

object TankChromosome {
  def apply(s: List[Any]) = {
    new TankChromosome(
      s(0).asInstanceOf[Seq[Seq[Double]]],
      s(1).asInstanceOf[Seq[Seq[Seq[Double]]]],
      s(2).asInstanceOf[TankSight],
      s(3).asInstanceOf[Double],
      s(4).asInstanceOf[Double],
      s(5).asInstanceOf[Seq[String]],
      s(6).asInstanceOf[StructureBuilder]
    )
  }
}

/**
 * Object used to define the sight of the Tank
 *
 * @param shape The polygon itself, the tank can sense what crosses the edges
 * @param center Center of sight. The polygon will be moved of that vector
 */
case class TankSight(shape: Polygon, center: Vector2D)

/**
 * Support class used as an interface between the output of the NN and the `Tank`
 */
object BrainOutputHelper {
  val count = 4
}

class BrainOutputHelper(outputs: Seq[Double]) extends OutputHelper[Double](outputs) {
  val force = Vector2D.new_xy(outputs(0), outputs(1))
  val rotation = outputs(2)
  val shoot = outputs(3)
}

/**
 * Support class used as an interface between the `Tank` and the input of the NN
 */
object BrainInputHelper {
  val count = 7
}

class BrainInputHelper(world: World, pos: Vector2D, speed: Vector2D, rot: Vector2D, sightCrossed: Vector2D) extends InputHelper[Double] {

  def this(world: World, rawSequence: Seq[Double]) = this(
    world,
    Vector2D.new_xy(rawSequence(0), rawSequence(1)),
    Vector2D.new_xy(rawSequence(2), rawSequence(3)),
    Vector2D.new_rt(1, rawSequence(4)),
    Vector2D.new_xy(rawSequence(5), rawSequence(6))
  )

  override protected val _values = Seq(
    5 * pos.x / world.arena.topRight.x, 5 * pos.y / world.arena.topRight.y,
    5 * speed.x / world.max_tank_speed, 5 * speed.y / world.max_tank_speed,
    rot.t,
    5 * sightCrossed.r / world.max_sight, 5 * sightCrossed.t / (2 * Math.PI)
  )
}