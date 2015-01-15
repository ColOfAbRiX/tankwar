package com.colofabrix.scala.neuralnetwork.builders.abstracts

/**
 * Created by Fabrizio on 14/01/2015.
 */
trait DataReader {
  def nextLayerReader: LayerReader
}
