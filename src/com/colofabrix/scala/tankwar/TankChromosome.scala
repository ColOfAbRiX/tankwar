package com.colofabrix.scala.tankwar

import com.colofabrix.scala.neuralnetwork.builders.abstracts.{BehaviourBuilder, DataReader}

/**
 * A TankChromosome contains all the data needed to uniquely identify
 * a Tank from another. Tank with identical chromosomes behave in the
 * same way
 */
case class TankChromosome(
  biases: Seq[Seq[Double]],
  weights: Seq[Seq[Seq[Double]]],
  activationFunction: Seq[String],
  valueRange: Double,
  brainBuilder: BehaviourBuilder,
  mass: Double
)


/**
 * Data used to initialise a new Tank
 * 
 * @param brainBuilder Builder for the brain of the tank
 * @param dataReader Data to build the brain of the tank
 * @param valueRange The range of the weights in the brain
 */
case class TankCreationData(
  brainBuilder: BehaviourBuilder,
  dataReader: DataReader,
  valueRange: Double,
  mass: Double
)
