package com.colofabrix.scala.neuralnetwork.builders

import scala.util.Random

abstract class WeightBuilder {
  def build: Seq[Seq[Double]]
}

class RandomWeight( neuron_count: Int, n_inputs: Int) extends WeightBuilder {
  override def build = Seq.fill(neuron_count, n_inputs)(Random.nextDouble * 2 - 1)
}

class DirectWeightBuilder( neuron_count: Int, n_inputs: Int)  extends WeightBuilder {
  override def build = Seq.fill(neuron_count, n_inputs)(Random.nextDouble * 2 - 1)
}

class ListWeightBuilder( neuron_count: Int, n_inputs: Int, weights: Seq[Seq[Double]] ) extends WeightBuilder {
  require( weights.length == neuron_count && weights.foldLeft(true)( _ & _.length == n_inputs ) )
  
  override def build = weights
}