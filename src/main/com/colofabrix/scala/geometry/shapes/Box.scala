package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.abstracts.Container
import com.colofabrix.scala.math.Vector2D

/**
  * Rectangle shape with edges parallel to the cartesian axis
  *
  * This kind of shape is particularly useful in checking overlaps and collisions
  * as it is done in constant time O(k) and without complex mathematical operations
  *
  * @param bottomLeft Rectangle left-bottom-most point
  * @param topRight Rectangle right-top point
  */
case class Box(bottomLeft: Vector2D, topRight: Vector2D )
extends ConvexPolygon(
   Seq(bottomLeft,
     Vector2D.new_xy(bottomLeft.x, topRight.y),
     topRight,
     Vector2D.new_xy(topRight.x, bottomLeft.y)
   ) )
with Container
{
  require( bottomLeft.x < topRight.x && bottomLeft.y < topRight.y, "The points of the rectangle must respect an order" )

  /**
   * Determines if a point is inside or on the boundary the shape
   *
   * @param p The point to be checked
   * @return True if the point is inside the shape
   */
  override def overlaps( p: Vector2D ) = {
    p.x >= bottomLeft.x && p.x <= topRight.x &&
    p.y >= bottomLeft.y && p.y <= topRight.y
  }

  /**
  * Width of the rectangle
  */
  val width = topRight.x - bottomLeft.x

  /**
  * Height of the rectangle
  */
  val height = topRight.y - bottomLeft.y

  /**
   * Area of the box
   */
  override lazy val area = width * height
}
