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
import com.colofabrix.scala.geometry.shapes._
import com.colofabrix.scala.math.{ RTVect, XYVect }

import scala.util.{ Random => Rnd }

/**
  *
  */
object ShapeUtils {

  /**
    * Returns a random Vect included in the specified area
    *
    * @param maxArea The area where the new vector must lie
    * @return A new instance of Vect
    */
  def rndVect( maxArea: Box ) = XYVect(
    Rnd.nextDouble * maxArea.width + maxArea.bottomLeft.x,
    Rnd.nextDouble * maxArea.height + maxArea.bottomLeft.y
  )

  /**
    * Creates a Seg in a random position inside the specified area
    *
    * @param area The area where the new circle must lie
    * @return A new instance of Seg
    */
  def rndSeg( area: Box ): Seg = {
    Seg( rndVect( area ), rndVect( area ) )
  }

  /**
    * Creates a Circle in a random position and with a random radius inside the specified area
    *
    * @param area The area where the new circle must lie
    * @return A new instance of Circle
    */
  def rndCircle( area: Box ): Circle = {
    val radius = Rnd.nextDouble * 0.999 * Math.min( area.width, area.height ) / 2.0
    val safeArea = Box( area.width - 2.0 * radius, area.height - 2.0 * radius ).move( area.bottomLeft + radius )
    val center = rndVect( safeArea )

    Circle( center, radius )
  }

  /**
    * Create a Box of random position and size inside the specified area
    *
    * @param area The area where the new box must lie
    * @return A new instance of Box
    */
  def rndBox( area: Box ): Box = Box( rndVect( area ), rndVect( area ) )

  /**
    * Create a Polygon of random position, size and number of vertices inside the specified area
    *
    * @param area The area where the new Polygon must lie
    * @return A new instance of Polygon
    */
  def rndPolygon( area: Box ): Polygon = {
    val rndVertices = Rnd.nextInt( 10 ) + 3
    val vertices = ( 0 until rndVertices ).map( _ ⇒ rndVect( area ) )
    new Polygon( vertices )
  }

  /**
    * Create a Polygon of random position, size and number of vertices inside the specified area
    *
    * The actual convex polygon is a regular polygon with constructed on a random circle
    *
    * @param area The area where the new Polygon must lie
    * @return A new instance of Polygon
    */
  def rndConvexPolygon( area: Box ): ConvexPolygon = {
    val numVertices = Rnd.nextInt( 10 ) + 3
    val encircling = rndCircle( area )

    val angularDistance = 2.0 * Math.PI / numVertices.toDouble

    val vertices = Seq.tabulate( numVertices ) { i ⇒
      RTVect( encircling.radius, angularDistance * i ) + encircling.center
    }

    new ConvexPolygon( vertices )
  }

  /**
    * Create a list of random shapes returning an element for each type of shapes
    *
    * @param area The area where the new Polygon must lie
    * @return A new instance of List containing a random shape of each possible type
    */
  def rndSequence( area: Box ): Seq[Shape] = {
    List(
      rndCircle( area ),
      rndPolygon( area ),
      rndConvexPolygon( area ),
      rndBox( area )
    )
  }
}
