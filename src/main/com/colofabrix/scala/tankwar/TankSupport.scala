package com.colofabrix.scala.tankwar

import com.colofabrix.scala.math.Vector2D
import com.colofabrix.scala.neuralnetwork.old.abstracts.{InputHelper, OutputHelper}
import com.colofabrix.scala.neuralnetwork.old.builders.abstracts.StructureBuilder

/**
 * A TankChromosome contains all the data needed to uniquely identify
 * a Tank from another. Tank with identical chromosomes behave in the
 * same way
 */
case class TankChromosome(
  biases: Seq[Seq[Double]],
  weights: Seq[Seq[Seq[Double]]],
  rotationRef: Double,
  sightRatio: Double,
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
    biases, weights, rotationRef, sightRatio, valueRange, activationFunction, brainBuilder
  )
}

object TankChromosome {
  def apply(s: List[Any]) = {
    new TankChromosome(
      s(0).asInstanceOf[Seq[Seq[Double]]],
      s(1).asInstanceOf[Seq[Seq[Seq[Double]]]],
      s(2).asInstanceOf[Double],
      s(3).asInstanceOf[Double],
      s(4).asInstanceOf[Double],
      s(5).asInstanceOf[Seq[String]],
      s(6).asInstanceOf[StructureBuilder]
    )
  }
}

/**
 * Support class used as an interface between the output of the NN and the `Tank`
 */
object BrainOutputHelper {
  val count = 4
}

/**
 * Support class used as an interface between the output of the NN and the `Tank`
 */
class BrainOutputHelper(outputs: Seq[Double]) extends OutputHelper[Double](outputs) {
  val force = Vector2D.new_xy(outputs(0), outputs(1))
  val rotation = outputs(2)
  val shoot = outputs(3)
}

/**
 * Support class used as an interface between the `Tank` and the input of the NN
 */
object BrainInputHelper {
  val count = 13
}

/**
 * Support class used as an interface between the `Tank` and the input of the NN
 */
class BrainInputHelper(world: World, pos: Vector2D, speed: Vector2D, rot: Vector2D, seenTanksPos: Vector2D, seenTanksSpeed: Vector2D, seenBulletsPos: Vector2D, seenBulletsSpeed: Vector2D) extends InputHelper[Double] {

  def this(world: World, rawSequence: Seq[Double]) = this(
    world,
    Vector2D.new_xy(rawSequence(0), rawSequence(1)),
    Vector2D.new_xy(rawSequence(2), rawSequence(3)),
    Vector2D.new_rt(1, rawSequence(4)),
    Vector2D.new_xy(rawSequence(5), rawSequence(6)),
    Vector2D.new_xy(rawSequence(7), rawSequence(8)),
    Vector2D.new_xy(rawSequence(9), rawSequence(10)),
    Vector2D.new_xy(rawSequence(11), rawSequence(12))
  )

  override protected val _values = Seq(
    pos.x / world.arena.topRight.x, pos.y / world.arena.topRight.y,
    speed.x / world.max_tank_speed, speed.y / world.max_tank_speed,
    rot.t / (2.0 * Math.PI),
    seenTanksPos.r / world.max_sight, seenTanksPos.t / (2.0 * Math.PI),
    seenTanksSpeed.r / world.max_tank_speed, seenTanksSpeed.t / (2.0 * Math.PI),
    seenBulletsPos.r / world.max_sight, seenBulletsPos.t / (2.0 * Math.PI),
    seenBulletsSpeed.r / world.max_bullet_speed, seenBulletsSpeed.t / (2.0 * Math.PI)
  )
}