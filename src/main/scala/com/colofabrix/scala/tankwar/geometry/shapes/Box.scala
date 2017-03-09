/*
 * Copyright (C) 2017 Fabrizio
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

package com.colofabrix.scala.tankwar.geometry.shapes

import java.util.concurrent.ConcurrentHashMap

import com.colofabrix.scala.math.{ DoubleWithAlmostEquals, Vect, XYVect }
import com.colofabrix.scala.tankwar.geometry.{ Shape, ShapesOverlapping }

import scala.collection.JavaConverters._

/**
  * Rectangle shape with edges parallel to the cartesian axis
  *
  * This kind of shape is particularly useful in checking overlaps and collisions as it
  * is done in constant time O(k) and without complex mathematical operations. For this
  * reasons, more than being only a {ConvexPolygon} it is also a {Container} to implement
  * an AABB collision detection.
  * See: http://stackoverflow.com/questions/22512319/what-is-aabb-collision-detection
  *
  * @see http://geomalgorithms.com/a08-_containers.html
  *
  * @param bottomLeft Rectangle left-bottom-most point, in any quadrant of the plane
  * @param topRight   Rectangle right-top point, in any quadrant of the plane
  */
class Box protected(val bottomLeft: Vect, val topRight: Vect) extends Shape {

  /** The vertices of the Box */
  val vertices: Seq[Vect] = bottomLeft ::
    XYVect(bottomLeft.x, topRight.y) ::
    topRight ::
    XYVect(topRight.x, bottomLeft.y) :: Nil

  /** Edges of the Box, built from the vertices. Edges are {Vect} from one vertex to its adjacent one */
  val edges: Seq[(Vect, Vect)] = (vertices(0), vertices(1)) ::
    (vertices(1), vertices(2)) ::
    (vertices(2), vertices(3)) ::
    (vertices(3), vertices(0)) :: Nil

  /** Height of the rectangle */
  val height = topRight.y - bottomLeft.y

  /** Width of the rectangle */
  val width = topRight.x - bottomLeft.x

  override val area = width * height

  /** Center of the Box */
  val center = bottomLeft + XYVect(width / 2.0, height / 2.0)

  /** The vertex that is closest to the origin of the axes. */
  lazy val origin = vertices.minBy(_.ρ)

  /** The vertex that is farthest to the origin of the axes. */
  lazy val opposite = vertices.maxBy(_.ρ)

  /** Rectangle top-left-most point, in any quadrant of the plane */
  val topLeft = XYVect(bottomLeft.x, topRight.y)

  /** Rectangle bottom-right-most point, in any quadrant of the plane */
  val bottomRight = XYVect(topRight.x, bottomLeft.y)

  /** Rectangle top-most Y */
  val top = topRight.y

  /** Rectangle bottom-most Y */
  val bottom = bottomLeft.y

  /** Rectangle left-most X */
  val left = bottomLeft.x

  /** Rectangle right-most X */
  val right = topRight.x

  override def move(where: Vect): Box = Box(bottomLeft + where, topRight + where)

  override def distance(p: Vect): Option[Vect] = {
    if( this.borderOrInside(p) ) {
      None
    }
    else {
      val dx = if( p.x ~< this.origin.x ) {
        p.x - this.origin.x
      }
      else if( p.x ~> this.opposite.x ) {
        p.x - this.opposite.x
      }
      else {
        0.0
      }

      val dy: Double = if( p.y ~< this.origin.y ) {
        p.y - this.origin.y
      }
      else if( p.y ~> this.opposite.y ) {
        p.y - this.opposite.y
      }
      else {
        0.0
      }

      Some(XYVect(dx, dy))
    }
  }

  override def inside(p: Vect) = {
    (p.x ~> bottomLeft.x) &&
      (p.x ~< topRight.x) &&
      (p.y ~> bottomLeft.y) &&
      (p.y ~< topRight.y)
  }

  override def border(p: Vect): Boolean =
    ((p.x ~== bottomLeft.x) || (p.x ~== topRight.x)) && (p.y ~< topRight.y) && (p.y ~> bottomLeft.y) ||
      ((p.y ~== bottomLeft.y) || (p.y ~== topRight.y)) && (p.x ~< topRight.x) && (p.x ~> bottomLeft.x)

  /**
    * Splits a rectangular area in different boxes
    *
    * The area is divided in equal parts as specified by the parameters
    *
    * @param hSplit Number of horizontal divisions
    * @param vSplit Number of vertical divisions
    *
    * @return A list of Box that cover the area
    */
  def split(hSplit: Int, vSplit: Int): Seq[Box] = {
    val width = this.width / hSplit
    val height = this.height / vSplit

    val templateBox =
      Box(Vect.zero, XYVect(width, height))
        .move(this.bottomLeft)

    for( j ← 0 until hSplit;
         i ← 0 until vSplit ) yield {
      templateBox.move(XYVect(width * i, height * j))
    }
  }

  override def toString = s"Box($bottomLeft, $topRight)"

  override def equals(other: Any): Boolean = other match {
    case that: Box ⇒
      bottomLeft == that.bottomLeft && topRight == that.topRight
    case _ ⇒ false
  }

  override def hashCode(): Int = {
    val state = Seq(bottomLeft, topRight)
    state.map(_.hashCode()).foldLeft(0)((a, b) ⇒ 31 * a + b)
  }
}

object Box {

  /**
    * Constructor that uses width, height and centers the Box at a specific point
    *
    * @param center Center of the box
    * @param width  Width of the box
    * @param height Height of the box
    */
  def apply(center: Vect, width: Double, height: Double): Box = {
    Box(
      XYVect(center.x - width / 2.0, center.y - height / 2.0),
      XYVect(center.x + width / 2.0, center.y + height / 2.0)
    )
  }

  /**
    * Constructor that uses width, height and starts the box at the origin of the axis.
    *
    * The width and height can be negative, so it's possible to create a Box on all the quadrants of the plane
    *
    * @param width  Width of the box, can be negative
    * @param height Height of the box, can be negative
    */
  def apply(width: Double, height: Double): Box = Box(Vect.zero, XYVect(width, height))

  /**
    * Creates a new Box using the two opposite vertices
    *
    * @param p0 The first vertex of the Box
    * @param p1 The second vertex of the Box opposite to p0
    *
    * @return
    */
  def apply(p0: Vect, p1: Vect): Box = {
    val topX = Math.max(p0.x, p1.x)
    val topY = Math.max(p0.y, p1.y)
    val bottomX = Math.min(p0.x, p1.x)
    val bottomY = Math.min(p0.y, p1.y)

    require(topX - bottomX > 0.0, "A Box must have a width greater than zero")
    require(topY - bottomY > 0.0, "A Box must have a height greater than zero")

    return new Box(XYVect(bottomX, bottomY), XYVect(topX, topY))
  }

  /**
    * Distributes the objects in the buckets that contain it.
    *
    * It is the most expensive function of the data structure, use it with care!
    *
    * @param nodes   The list of all the buckets that cover the whole area
    * @param shapes  The Shapes to spread into the buckets
    * @param compact If true the function will not include in the output Boxes with empty content
    *
    * @return A Map that connects the boxes with a list of objects that contains. Objects can be present in multiple
    *         buckets
    */
  def spreadAcross(nodes: Seq[Box], shapes: Seq[Shape], compact: Boolean = true): Map[Box, Seq[Shape]] = {
    val acc = new ConcurrentHashMap[Box, Seq[Shape]].asScala

    for( b ← nodes.par ) {
      val objInBox: Seq[Shape] = shapes.filter { s =>
        ShapesOverlapping.borderOrInside(b, s)
      }

      if( objInBox.nonEmpty || !compact ) acc += ((b, objInBox))
    }

    return acc.toMap
  }
}
