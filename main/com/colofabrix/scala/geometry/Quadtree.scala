package com.colofabrix.scala.geometry

import com.colofabrix.scala.geometry.shapes.{Box, ConvexPolygon}

/**
 * Quadtree implementation
 *
 * A quadtree is a try of tree with 4 children nodes per parent used
 * to partition a cartesian plane and speed up object-object interactions
 * in graphical environments
 */
class Quadtree[T <: ConvexPolygon](val dimensions: Box, val I: Quadtree[T], val II: Quadtree[T], val III: Quadtree[T], val IV: Quadtree[T], val elements: Seq[T] = Seq()) {

  def insert(o: T): Quadtree[T] = ???

  def delete(o: T): Quadtree[T] = ???

  def update(o: T): Quadtree[T] = ???

  def build(objects: Seq[T]): Quadtree[T] = ???

  def notFullyContained(o: T): Boolean =
    o.vertices.foldLeft(false){ (r, v) ⇒ r || !dimensions.overlaps(v) }

  def collision(o: T): Option[T] = Option.empty

}
