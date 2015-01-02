package com.colofabrix.scala.neuralnetwork.tests

import com.colofabrix.scala.neuralnetwork.GenericNeuralNetwork
import com.colofabrix.scala.neuralnetwork.layers._
import org.scalatest._

/**
 * Unit test for `GenericNeuralNetwork`
 *
 * Created by Fabrizio on 30/12/2014.
 */
class UTNeuralNetwork extends WordSpec with Matchers {

  // Range of test values
  private def linear( x: Double ) = x
  private val inputs_range = -1.1 to (1.1, 0.2)
  private val tolerance = 1E-05

  // Various test layers
  private val input_layer = new InputLayer(1)
  private val hidden_layer_1 = new HiddenLayer( linear, 1, 2, Seq(-1.0, 2.0), Seq(Seq(-3.0), Seq(4.0)) )
  private val hidden_layer_2 =  new HiddenLayer( linear, 2, 3, Seq(-1.0, 0.0, 2.0), Seq(Seq(-3.0, -4.0), Seq(0.0, 0.0), Seq(5.0, 6.0)) )
  private val output_layer = new OutputLayer( linear, 3, 1, Seq(0.0), Seq(Seq(-1.0, 0.0, 2.0)) )
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
  private def test_with_default(nn: GenericNeuralNetwork) = {

    inputs_range foreach { i =>
      val hd1 = (
        -3.0 * i - 1.0,
        4.0 * i + 2.0)
      val hd2 = (
        -3.0 * hd1._1 - 4.0 * hd1._2 - 1.0,
        0.0 * hd1._1 + 0.0 * hd1._2 + 0.0,
        5.0 * hd1._1 + 6.0 * hd1._2 + 2.0)
      val out = -1.0 * hd2._1 + 0.0 * hd2._2 + 2.0 * hd2._3 + 0.0

      nn.output(i)(0) should equal(out +- tolerance)
    }

  }

  "Initialization" should {

    "Respect structural constraints" in {

      // When an input layer is not provided
      intercept[IllegalArgumentException] {
        new GenericNeuralNetwork( null )
      }

      // When a hidden sequence is not provided
      intercept[IllegalArgumentException] {
        new GenericNeuralNetwork( input_layer, null, output_layer )
      }

      // Checking input-output matching
      intercept[IllegalArgumentException] {
        new GenericNeuralNetwork( input_layer, Seq(hidden_layer_1), output_layer )
      }
    }

    "Be correct with only an input layer" in {
      val nn = new GenericNeuralNetwork( input_layer )
      inputs_range foreach { i => nn.output(i)(0) should equal (i) }
    }

  }

  "Output" must {

    "Be valid for a complete NeuralNetwork" in {
      test_with_default(new GenericNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer ))
    }

  }

  "Equals method" must {

    "Return true if two objects represent the same NN" in {
      val nn1 = new GenericNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )
      val nn2 = new GenericNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )

      nn1 should equal (nn2)
    }

    "Return false if two objects do not represent the same NN" in {
      val nn1 = new GenericNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )
      val nn2 = new GenericNeuralNetwork( input_layer )

      nn1 shouldNot equal (nn2)
    }

  }

  "Builder" when {

    "Used with only the number of inputs" must {

      "Produce valid outputs" in {
        val nn = new GenericNeuralNetwork(2)

        inputs_range foreach { i =>
          nn.output(Seq(i, i))(0) should equal(i)
          nn.output(Seq(i, i))(1) should equal(i)
        }
      }

    }

    "Used with collections" must {

      "Produce valid outputs" in {
        test_with_default( GenericNeuralNetwork(eq_biases, eq_weights, x => x) )
      }

      "Create a NN equivalent to a manual created one" in {
        val manual = new GenericNeuralNetwork( input_layer, Seq(hidden_layer_1, hidden_layer_2), output_layer )
        val built = GenericNeuralNetwork(eq_biases, eq_weights, x => x)

        built should equal (manual)
      }

    }

  }

}
