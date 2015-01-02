/**
 * Library for Neural Networks
 *
 * Created by Fabrizio on 20/11/2014.
 */
package com.colofabrix.scala.neuralnetwork

/**
 * Collection of activation functions for neural networks
 */
object ActivationFunctions {
  /**
   * Heaviside step function
   * See https://en.wikipedia.org/wiki/Heaviside_step_function
   */
  def step( input: Double ): Double = if ( input >= 0.0 ) 1.0 else 0.0
  
  /**
   * Sigmoid function
   * See https://en.wikipedia.org/wiki/Sigmoid_function
   */
  def sigmoid( input: Double ): Double = 1.0 / (1 + Math.exp( - input ))
  
  /**
   * Hyperbolic tangent function
   * See https://en.wikipedia.org/wiki/Hyperbolic_function
   */
  def tanh( input: Double ): Double = math.tanh( input )

  /**
   * Rectifier function
   * See https://en.wikipedia.org/wiki/Rectifier_%28neural_networks%29
   */
  def rectifier( input: Double ): Double = Math.max( 0, input )
  
  /**
   * Softplus function
   * See https://en.wikipedia.org/wiki/Rectifier_%28neural_networks%29
   */
  def softplus( input: Double ): Double = Math.log( 1 + Math.exp(input) )

  /**
   * Linear function
   *
   * Maps the input directly to the output
   */
  def linear( input: Double): Double = input
}