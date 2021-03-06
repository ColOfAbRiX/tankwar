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

package com.colofabrix.scala.gfx.abstracts

/**
  * A class that renders something to the screen.
  *
  * This "something" is dependent on the implementation and can be anything: a circle, a polygon, text, ...
  */
trait Renderer {

  /**
    * Draw the appropriate things on the screen given a specific drawing context
    *
    * The parameter `create` might be ignored from the implementation, depending on what the renderer is meant
    * to do and usually its behaviour is stated in the documentation
    *
    * @param create With a value of true a new drawing context will be create, with false nothing is done
    */
  def render( create: Boolean = true ): Unit

}