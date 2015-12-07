/*
 * Copyright (C) 2015 Fabrizio Colonna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.colofabrix.test.scala.neuralnetwork.old

import java.io.{ File, PrintWriter }

import com.colofabrix.scala.neuralnetwork.old.abstracts.{ ActivationFunction, NeuronLayer }
import com.colofabrix.scala.neuralnetwork.old.activationfunctions.{ Linear, Sigmoid }
import org.scalatest._

/**
 * Unit testing for `NeuronLayer`
 *
 * Created by Fabrizio on 17/12/2014.
 */
class LayerTest extends WordSpec with Matchers {


  /**
   * Concrete Layer class to test Layer functionality
   */
  private[LayerTest] class ConcreteNeuronLayer(
    n_inputs: Int = 1, n_outputs: Int = 1,
    bias: Seq[Double] = Seq( 0.0 ),
    weights: Seq[Seq[Double]] = Seq( Seq( 1.0 ) ),
    af: ActivationFunction = new Linear
  )
    extends NeuronLayer(
      af,
      n_inputs, n_outputs,
      bias, weights
    )


  // Range of test values
  private val inputs_count = 1 to 3
  private val outputs_count = 1 to 3
  private val inputs_range = -2.0 to(2.0, 0.1)
  private val weights_range = -2.1 to(2.1, 0.5)
  private val bias_range = weights_range
  private val tolerance = 1E-05

  "Initialization" should {

    "Respect structural constraints" in {

      // Check inputs count
      intercept[IllegalArgumentException] {
        new ConcreteNeuronLayer( n_inputs = 0 )
      }

      // Check output count
      intercept[IllegalArgumentException] {
        new ConcreteNeuronLayer( n_outputs = 0 )
      }

      // Check various count constraints
      for( i <- inputs_count; o <- outputs_count ) {
        // Check bias count
        intercept[IllegalArgumentException] {
          new ConcreteNeuronLayer( i, o, List.fill( o + 1 )( 0.0 ), List.fill( o, i )( 1.0 ) )
        }

        // Check weights count against the number of neurons
        intercept[IllegalArgumentException] {
          new ConcreteNeuronLayer( i, o, List.fill( o )( 0.0 ), List.fill( o + 1, i )( 1.0 ) )
        }

        // Check weights count against the number of inputs
        intercept[IllegalArgumentException] {
          new ConcreteNeuronLayer( i, o, List.fill( o )( 0.0 ), List.fill( o, i + 1 )( 1.0 ) )
        }
      }
    }

  }

  "Equals method" must {

    "Return true if two objects represent the same layer" in {
      val layer1 = new ConcreteNeuronLayer( 1, 2, Seq( 1.0, 2.0 ), Seq( Seq( -1.0 ), Seq( -2.0 ) ) )
      val layer2 = new ConcreteNeuronLayer( 1, 2, Seq( 1.0, 2.0 ), Seq( Seq( -1.0 ), Seq( -2.0 ) ) )

      layer1 should equal( layer2 )
    }

    "Return false if two objects do not represent the same layer" in {
      val layer1 = new ConcreteNeuronLayer( 1, 2, Seq( 1.0, 2.0 ), Seq( Seq( -1.0 ), Seq( -2.0 ) ) )
      val layer2 = new ConcreteNeuronLayer( 1, 2, Seq( 2.0, 2.0 ), Seq( Seq( -1.0 ), Seq( -2.0 ) ) )

      layer1 shouldNot equal( layer2 )
    }

  }

  "Inputs" must {

    "Be carried to the output" in {

      // Check just for input counts from 1 to 4, it should be more than enough
      inputs_count foreach { n =>

        val layer = new ConcreteNeuronLayer( 1, n, Seq.fill( n )( 0.0 ), Seq.fill( n, 1 )( 1.0 ) )

        inputs_range foreach { x =>

          withClue( s"While checking with n=$n, x=$x," ) {
            layer.output( x ) foreach { _ should equal( x ) }
          }
        }
      }
    }

  }

  "Neuron" should {

    "Be tested graphically" in {

      val writer = new PrintWriter( new File( """out/activation-function.csv""" ) )

      List( 1, 5, 25 ) foreach { b =>
        List( 1, 5, 25 ) foreach { w =>
          writer.write( s"b=$b, w=$w\n" )
          inputs_range foreach { i =>
            writer.write( s"$i;".replace( ".", "," ) )
            val layer = new ConcreteNeuronLayer( 1, 1, Seq( b ), Seq( Seq( w ) ), ActivationFunction( "sigmoid" ) )
            writer.write( layer.output( i )( 0 ).toString.replace( ".", "," ) + ";\n" )
          }
        }
      }

      writer.close( )

    }

  }

  "Outputs" must {

    "Match the Layer setup" in {

      inputs_count foreach { n =>

        val layer = new ConcreteNeuronLayer( 1, n, Seq.fill( n )( 0.0 ), Seq.fill( n, 1 )( 1.0 ) )

        withClue( s"While checking with n=$n," ) {
          layer.output( 1.0 ).size should equal( n )
        }

      }
    }

    "Be a linear composition of inputs" in {

      for( w1 <- weights_range; w2 <- weights_range )
        for( b <- bias_range ) {
          val layer = new ConcreteNeuronLayer( 2, 1, Seq( b ), List( List( w1, w2 ) ) )

          for( i1 <- inputs_range; i2 <- inputs_range ) {
            withClue( s"While checking with b=$b, w=($w1, $w2), i=($i1, $i2)," ) {
              layer.output( Seq( i1, i2 ) )( 0 ) should equal( (w1 * i1 + w2 * i2 + b) +- tolerance )
            }
          }
        }
    }

    "Be valid for each output" in {

      for( w1 <- weights_range; w2 <- weights_range )
        for( b1 <- bias_range; b2 <- bias_range ) {

          val layer = new ConcreteNeuronLayer( 1, 2, Seq( b1, b2 ), List( List( w1 ), List( w2 ) ) )

          for( i <- inputs_range ) {

            withClue( s"While checking with b=$b1, w=($w1, $w2), i=$i, " ) {
              layer.output( i )( 0 ) should equal( (w1 * i + b1) +- tolerance )
              layer.output( i )( 1 ) should equal( (w2 * i + b2) +- tolerance )
            }
          }
        }
    }

    "Be valid for any ActivationFunction" in {

      for( w1 <- weights_range; w2 <- weights_range )
        for( b <- bias_range ) {
          val activation = new Sigmoid
          val layer = new ConcreteNeuronLayer( 2, 1, Seq( b ), List( List( w1, w2 ) ), activation )

          for( i1 <- inputs_range; i2 <- inputs_range ) {
            withClue( s"While checking with b=$b, w=($w1, $w2), i=($i1, $i2)," ) {
              layer.output( Seq( i1, i2 ) )( 0 ) should equal( activation( w1 * i1 + w2 * i2 + b ) +- tolerance )
            }
          }
        }
    }

  }

}
