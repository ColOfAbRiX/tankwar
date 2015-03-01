package com.colofabrix.scala.tankwar

import com.colofabrix.scala.geometry.Vector2D
import com.colofabrix.scala.geometry.shapes.Polygon
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
