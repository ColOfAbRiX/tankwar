package com.colofabrix.test.scala.neuralnetwork.old

import com.colofabrix.scala.neuralnetwork.old.FeedforwardNeuralNetwork
import com.colofabrix.scala.neuralnetwork.old.abstracts.ActivationFunction
import com.colofabrix.scala.neuralnetwork.old.layers.{HiddenLayer, InputLayer, OutputLayer}
import org.scalatest._

import scala.util.Random

/**
 * Unit test for `GenericNeuralNetwork`
 *
 * Created by Fabrizio on 30/12/2014.
 */
class NeuralNetworkTest extends WordSpec with Matchers {

  // Range of test values
  private val inputs_range: List[Double] = (-2.0 to (2.0, 0.1)).toList ::: List.fill(20)(Random.nextDouble * 10 - 5)
  private val tolerance = 1E-05

  // Various test layers
  private val activation = ActivationFunction("sigmoid")
  private val input_layer = new InputLayer(1)
  private val hidden_layer_1 = new HiddenLayer( activation, 1, 2, Seq(-1.0, 2.0), Seq(Seq(-3.0), Seq(4.0)) )
  private val hidden_layer_2 =  new HiddenLayer( activation, 2, 3, Seq(-1.0, 0.0, 2.0), Seq(Seq(-3.0, -4.0), Seq(0.0, 0.0), Seq(5.0, 6.0)) )
  private val output_layer = new OutputLayer( activation, 3, 1, Seq(0.0), Seq(Seq(-1.0, 0.0, 2.0)) )
  private val eq_biases = Seq(
    Seq(0.0),
    Seq(-1.0, 2.0),
    Seq(-1.0, 0.0, 2.0),
    Seq(0.0)
  )
  private val eq_weights = Seq(
    Seq(Seq(1.0)),
    Seq(Seq(-3.0), Seq(4.0)),
    Seq(Seq(-3.0, -4.0), Seq(0.0, 0.0), Seq(5.0, 6.0)),
    Seq(Seq(-1.0, 0.0, 2.0))
  )

  // Tests a NN with the default values
  private def test_with_default(nn: FeedforwardNeuralNetwork, activation: ActivationFunction = activation) {

    inputs_range foreach { i =>
      val in = i
      val hd1 = (
        activation(-3.0 * in - 1.0),
        activation(4.0 * in + 2.0) )
      val hd2 = (
        activation(-3.0 * hd1._1 - 4.0 * hd1._2 - 1.0),
        activation( 0.0 * hd1._1 + 0.0 * hd1._2 + 0.0),
        activation( 5.0 * hd1._1 + 6.0 * hd1._2 + 2.0) )
      val expected = activation(-1.0 * hd2._1 + 0.0 * hd2._2 + 2.0 * hd2._3 + 0.0)
      val output = nn.output(i)

      withClue( s"While i=$i, ") {
        output(0) should equal(expected +- tolerance)
      }
    }
  }

  "Initialization" should {

    "Respect structural constraints" in {

      // When an input layer is not provided
      intercept[IllegalArgumentException] {
        new FeedforwardNeuralNetwork( null )
      }

      // When a hidden sequence is not provided
      intercept[IllegalArgumentException] {
        new FeedforwardNeuralNetwork( input_layer, null, output_layer )
      }

      // Checking input-output matching
      intercept[IllegalArgumentException] {
        new FeedforwardNeuralNetwork( input_layer, Seq(hidden_layer_1), output_layer )
      }
    }

    "Be correct with only an input layer" in {
      val nn = new FeedforwardNeuralNetwork( input_layer )
      inputs_range foreach { i => nn.output(i)(0) should equal (i) }
    }

  }

  "Output" must {

    "Be valid for a complete NeuralNetwork" in {
      test_with_default(
        new FeedforwardNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )
      )
    }

  }

  "Equals method" must {

    "Return true if two objects represent the same NN" in {
      val nn1 = new FeedforwardNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )
      val nn2 = new FeedforwardNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )

      nn1 should equal (nn2)
    }

    "Return false if two objects do not represent the same NN" in {
      val nn1 = new FeedforwardNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )
      val nn2 = new FeedforwardNeuralNetwork( input_layer )

      nn1 shouldNot equal (nn2)
    }

  }

}
