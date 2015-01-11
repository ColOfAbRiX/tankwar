package com.colofabrix.scala.geometry.shapes

import com.colofabrix.scala.geometry.Vector2D

/**
  * Rectangle shape
  *
  * @param bottomLeft Rectangle left-bottom-most point
  * @param topRight Rectangle right-top point
  */
case class OrtoRectangle(bottomLeft: Vector2D, topRight: Vector2D )
  extends ConvexPolygon(
     Seq(bottomLeft,
       Vector2D.fromXY(bottomLeft.x, topRight.y),
       topRight,
       Vector2D.fromXY(topRight.x, bottomLeft.y)
     ) ) {
     require( bottomLeft.x < topRight.x && bottomLeft.y < topRight.y, "The points of the rectangle must respect an order" )

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
   }
