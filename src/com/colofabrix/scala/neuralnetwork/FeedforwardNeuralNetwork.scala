package com.colofabrix.scala.neuralnetwork

import com.colofabrix.scala.neuralnetwork.abstracts._
import com.colofabrix.scala.neuralnetwork.layers._

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
extends NeuralNetwork
  with Biased[Seq[Double]]
  with Weighted[Seq[Double]] {

  /**
   * Auxiliary constructor with implied InputLayer
   *
   * @param n_inputs Number of inputs
   * @param hidden_layers Zero or more hidden layers. If no output layer the output matches exactly the inputs
   * @param output_layer One output layer to collect the results
   */
  def this(n_inputs: Int, hidden_layers: Seq[HiddenLayer], output_layer: OutputLayer) = this( new InputLayer(n_inputs), hidden_layers, output_layer )

  def this(n_inputs: Int, hidden_layers: Seq[HiddenLayer]) = this( new InputLayer(n_inputs), hidden_layers, null )

  def this(n_inputs: Int) = this( new InputLayer(n_inputs), Seq(), null )

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
  override def output( inputs: Seq[T] ): Seq[T] = {
    // Calculate the output of one layer and use it to feed the next layer
    all_layers.foldLeft( inputs )( (input, layer) => layer.output(input) )
  }

  override def toString = {
    val text = "(" + this.input_layer.toString + ", " + this.hidden_layers.toString + ", " + this.output_layer.toString + ")"
    text.replace("List", "").replace("com.colofabrix.scala.neuralnetwork.", "")
  }
}

object FeedforwardNeuralNetwork {
  /**
   * Creates a `GenericNeuralNetwork` starting from its matrices of biases and weights
   *
   * The collection must contain all data for all the layers. This function will then split the collection into an
   * input, hidden and output layer. If there is only one item it will create an input layer. With 2 items it will
   * create an input and and output layer and with more than 2 items it will create all the others
   *
   * @param biases Full set of biases for the whole NN
   * @param weights Full set of weights for the whole NN
   * @param af Selected activation function
   * @return A `GenericNeuralNetwork` represented by the input data
   */
  def apply(biases: Seq[Seq[Double]], weights: Seq[Seq[Seq[Double]]], af: ActivationFunction): FeedforwardNeuralNetwork = {
    val combined = biases zip weights

    // These checks are done because we need a reliable set of data to extract information like the number of inputs
    require(af != null, "You must specify a non-null activation function")
    require(biases.length == weights.length, "Biases and weights must represent the same number of layers")
    require(biases.length > 0, "At least one layer must be specified")
    for ((b, w) <- combined) {
      require(b.length > 0 && b.length == w.length, "Bias and weights count must match the same number of neurons")
      require(w.forall(w(0).length == _.length), "All the weights must be for the same number of inputs")
    }

    // Input layer creation from the first index of the collections
    val input_layer = {
      val (b, w) = combined.head
      //new ExtendedInputLayer(af, w(0).length, w.length, b, w)
      new InputLayer(w(0).length)
    }
    if( biases.length == 1 )
      return new FeedforwardNeuralNetwork(input_layer)

    // Output layer from the last index of the collections
    val output_layer = {
      val (b, w) = combined.last
      new OutputLayer(af, w(0).length, w.length, b, w)
    }
    if( biases.length == 2 )
      return new FeedforwardNeuralNetwork(input_layer, Seq(), output_layer)

    // This only if there are hidden layers
    val hidden_layers =
      for ((b, w) <- combined.tail.init) yield
        new HiddenLayer(af, w(0).length, w.length, b, w)

    new FeedforwardNeuralNetwork(input_layer, hidden_layers, output_layer)
  }
}