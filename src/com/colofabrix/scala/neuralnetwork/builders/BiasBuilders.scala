package com.colofabrix.scala.neuralnetwork.builders

import scala.util.Random

abstract class BiasBuilder {
  def build: Seq[Double]
}

class RandomBiasBuilder( neuron_count: Int ) extends BiasBuilder {
  override def build = Seq.fill( neuron_count )( Random.nextDouble() )
}

class NoBiasBuilder( neuron_count: Int ) extends BiasBuilder {
  override def build = Seq.fill( neuron_count )( 0.0 )
}

class ListBiasBuilder( neuron_count: Int, biases: Seq[Double] ) extends BiasBuilder {
  require( biases.length == neuron_count )
  
  override def build = biases
}