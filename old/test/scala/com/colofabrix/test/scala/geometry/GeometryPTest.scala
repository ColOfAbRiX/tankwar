/*
 * Copyright (C) 2016 Fabrizio
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

package com.colofabrix.test.scala.geometry

import com.colofabrix.scala.geometry.abstracts.Shape
import com.colofabrix.scala.geometry.shapes.Box
import org.scalameter._
import org.scalameter.api.{ LoggingReporter, exec }
import org.scalameter.execution.LocalExecutor
import org.scalameter.picklers.Implicits._
import org.scalameter.reporting.HtmlReporter

/**
  *
  */
class GeometryPTest extends Bench[Double] {

  //
  // Configuration
  //

  override def executor = LocalExecutor(
    new Executor.Warmer.Default,
    Aggregator.median,
    measurer
  )

  override def measurer = new Measurer.Default

  override def reporter = Reporter.Composite(
    new LoggingReporter[Double],
    new HtmlReporter[Double]( true )
  )

  override def persistor = Persistor.None

  //
  // Inputs
  //

  protected def logarithmic( decades: Int, ppd: Int ) = for (
    d ← Gen.exponential( "decades" )( 1, Math.pow( 10.0, decades - 1.0 ).toInt, 10 );
    s ← Gen.range( "steps" )( 1, ppd - 1, 1 )
  ) yield { s * d }

  protected def shapes( f: Box ⇒ Shape )( generator: Gen[Int], area: Box ) =
    for ( s ← generator ) yield List.fill( s ) { f( area ) }

  protected def boxes( generator: Gen[Int], area: Box ) = shapes( ShapeUtils.rndBox )( generator, area )

  protected def polygon( generator: Gen[Int], area: Box ) =
    for( s ← generator ) yield List.fill( s ) {
      ShapeUtils.rndPolygon( area, Some( s ) )
    }

  private val testArea = Box( 600, 300 )

  //
  // Tests
  //

  performance of "Box" config(
    exec.maxWarmupRuns → 20,
    exec.benchRuns → 18
    ) in {
    val testBuckets = testArea.split( 3, 2 )

    measure method "spreadAcross" in {
      using( boxes( logarithmic( 3, 10 ), testArea ) ) in { s ⇒
        Box.spreadAcross( testBuckets, s, compact = false )
      }
    }

    measure method "intersects with Boxes" in {
      using( boxes( logarithmic( 3, 10 ), testArea ) ) in { s ⇒
        s.foreach( b ⇒ testArea.intersects( b ) )
      }

      using( polygon( Gen.range( "vertices" )( 3, 250, 1 ), testArea ) ) in { s ⇒
        s.foreach( b ⇒ testArea.intersects( b ) )
      }
    }

  }
}