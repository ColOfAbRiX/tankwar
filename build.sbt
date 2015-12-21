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
/*
 * Tankwar SBT Build File
 * Ref: http://www.scala-sbt.org/0.13.0/docs/Examples/Quick-Configuration-Examples.html
 */

// Projecty Definition
lazy val root = (project in file(".")).
  settings(
    name := "TankWar",
    version := "0.2.0",
    scalaVersion := "2.11.7",
    mainClass in Compile := Some("com.colofabrix.scala.TankWarMain")
  )

// Dependencies
libraryDependencies ++= Seq(
  "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.9.0" classifier "natives-windows" classifier "natives-linux" classifier "natives-osx",
  "slick-util" % "slick-util" % "1.0.0" from "http://slick.ninjacave.com/slick-util.jar",
  "org.uncommons.watchmaker" % "watchmaker-framework" % "0.7.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.uncommons" % "uncommons-maths" % "1.2",
  "org.lwjgl.lwjgl" % "lwjgl_util" % "2.9.0"
)

// Scala compiler options
scalacOptions ++= Seq(
  "-Xmax-classfile-name", "72",
  "-deprecation",
  "-feature"
)

// Native libraries extraction - LWJGL has some native libraries provided as JAR files that I have to extract
compile in Compile <<= (compile in Compile).dependsOn(Def.task {
  val r = "^(\\w+).*".r
  val r(os) = System.getProperty( "os.name" )

  val jars = ( update in Compile ).value
    .select( configurationFilter( "compile" ) )
    .filter( _.name.contains( os.toLowerCase ) )

  jars foreach { jar =>
    println( s"[info] Processing '$jar' and saving to '${unmanagedBase.value}'" )
    IO.unzip(  jar, unmanagedBase.value )
  }

  Seq.empty[File]
})

// META-INF discarding
/*
mergeStrategy in assembly <<= (mergeStrategy in assembly)( old => {
  case PathList( "META-INF", xs@_* ) => MergeStrategy.discard
  case x => MergeStrategy.first
})
*/

assemblyMergeStrategy in assembly := {
  case PathList( "META-INF", xs@_* ) => MergeStrategy.discard
  case x => MergeStrategy.first
}