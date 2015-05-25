package com.colofabrix.scala.neuralnetwork.old

import com.colofabrix.scala.neuralnetwork.old.abstracts.{NeuronLayer, NeuralNetwork}
import com.colofabrix.scala.neuralnetwork.old.layers.{InputLayer, OutputLayer, HiddenLayer}
import com.colofabrix.scala.neuralnetwork.old.abstracts._
import com.colofabrix.scala.neuralnetwork.old.layers._

/**
 * A generic neural network model
 *
 * This class is used to model any NN. It consists of layer of neurons `Layer`
 * connected between each other, an input and an output layer.
 *
 * @param input_layer One input layer of the NN
 * @param hidden_layers Zero or more hidden layers. If no output layer the output matches exactly the inputs
 * @param output_layer One output layer to collect the results
 */
class FeedforwardNeuralNetwork(
  val input_layer: InputLayer,
  val hidden_layers: Seq[HiddenLayer] = Seq(),
  val output_layer: OutputLayer = null)
extends NeuralNetwork {

  override type U = Seq[Double]

  override type V = Seq[String]

  // Input and Output layers are required. Also the HiddenLayer sequence is required, but can be empty
  require( input_layer != null, "The input layer must not be null" )
  require( hidden_layers != null, "You must specify at least an empty set of hidden layers" )

  /**
   * Single variable containing all the layer as a sequence of `NeuronLayer`
   *
   * Internally there is no distinction between input/hidden/output layers to allow uniformity
   */
  protected val all_layers: Seq[NeuronLayer] = {
    if( output_layer == null )
      input_layer :: hidden_layers.toList ::: Nil
    else
      input_layer :: hidden_layers.toList ::: output_layer :: Nil
  }

  override val biases = for( layer <- all_layers ) yield layer.biases

  override val weights = for (layer <- all_layers ) yield layer.weights

  override val activationFunction = for (layer <- all_layers ) yield layer.activationFunction

  override val n_inputs = all_layers.head.n_inputs

  override val n_outputs = all_layers.last.n_outputs

  override def equals( other: Any ) = other match {
    case that: FeedforwardNeuralNetwork =>
      this.canEqual(that) &&
      this.all_layers == that.all_layers
    case _ => false
  }

  override def hashCode: Int = {
    41 + this.all_layers.hashCode
  }

  def canEqual( other: Any ): Boolean =
    other.isInstanceOf[FeedforwardNeuralNetwork]

  // It checks that the number of inputs of the layer N + 1 equals the number of neurons of the layer N
  require(
    all_layers.size == 1 ||
    all_layers.iterator.sliding(2).forall {
      layer => layer(0).n_outputs == layer(1).n_inputs
    }, "Input/outputs count didn't match between layers"
  )

  /**
   * Calculate the output of the NN
   *
   * Given a set of input values it calculates the set of output values
   *
   * @param inputs A sequence of double to feed the NN
   * @return A sequence of double representing the output
   */
  override def output( inputs: Seq[Double] ): Seq[Double] = {
    // Calculate the output of one layer and use it to feed the next layer
    all_layers.foldLeft( inputs )( (input, layer) => layer.output(input) )
  }

  /**
   * Gets a string representation of the neural network
   *
   * @return A string containing the representation of weights and biases of the neural network
   */
  override def toString = {
    val text = this.getClass + "(" + this.input_layer.toString + ", " + this.hidden_layers.toString + ", " + this.output_layer.toString + ")"
    text.replace("class ", "").replace("List", "").replace("com.colofabrix.scala.neuralnetwork.", "")
  }
}