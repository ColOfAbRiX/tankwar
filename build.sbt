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

import com.typesafe.sbt.SbtScalariform._
import de.johoop.cpd4sbt.CopyPasteDetector._
import sbt.File
import sbt.Keys._
import wartremover.WartRemover.autoImport._

import scalariform.formatter.preferences._

// Project Definition

name := "TankWar"
version := "0.2.0"
scalaVersion := "2.11.7"
mainClass in Compile := Some("com.colofabrix.scala.TankWarMain")
fork := true

// Dependencies
resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies ++= Seq(
  "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.9.0" classifier "natives-windows" classifier "natives-linux" classifier "natives-osx",
  "slick-util" % "slick-util" % "1.0.0" from "http://slick.ninjacave.com/slick-util.jar",
  "com.github.wookietreiber" %% "scala-chart" % "latest.integration",
  "org.uncommons.watchmaker" % "watchmaker-framework" % "0.7.1",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "com.storm-enroute" %% "scalameter" % "0.7",
  "org.uncommons" % "uncommons-maths" % "1.2",
  "org.lwjgl.lwjgl" % "lwjgl_util" % "2.9.0"
)

// Scala compiler options.
// See: https://tpolecat.github.io/2014/04/11/scalac-flags.html

scalacOptions ++= Seq(
  "-Xmax-classfile-name", "72",
  "-encoding", "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  //"-Xfatal-warnings",
  "-Xfuture",
  "-Xlint",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:higherKinds",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code"
)

// JVM Options.
// See: http://blog.sokolenko.me/2014/11/javavm-options-production.html

javaOptions ++= Seq(
  "-server",
  // Memory settings
  "-XX:InitialHeapSize=1G",
  "-XX:MaxHeapSize=1G",
  "-XX:NewRatio=1",
  "-XX:MetaspaceSize=32M",
  "-XX:MaxMetaspaceSize=32M",
  "-XX:SurvivorRatio=10",
  "-XX:CompressedClassSpaceSize=16M",
  "-XX:+UseCompressedOops",
  "-XX:+UseCompressedClassPointers",
  // Other settings
  s"-Djava.library.path=${unmanagedBase.value}"
)

// Scaladoc configuration

target in (Compile, compile) in doc := baseDirectory.value / "docs"

// Native libraries extraction - LWJGL has some native libraries provided as JAR files that I have to extract

compile in Compile <<= (compile in Compile).dependsOn(Def.task {
  val r = "^(\\w+).*".r
  val r(os) = System.getProperty( "os.name" )

  val jars = ( update in Compile ).value
    .select( configurationFilter( "compile" ) )
    .filter( _.name.contains( os.toLowerCase ) )

  jars foreach { jar =>
    println( s"[info] Processing '${jar.getName}' and saving to '${unmanagedBase.value}'" )
    IO.unzip(  jar, unmanagedBase.value )
  }

  Seq.empty[File]
})

// Code Style

/*
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")
compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value
(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

scalastyleConfig := file( s"${sourceDirectory.value}/main/resources/scalastyle-config.xml" )
*/

SbtScalariform.scalariformSettings ++ Seq(
  ScalariformKeys.preferences := ScalariformKeys.preferences.value
    .setPreference(RewriteArrowSymbols, true)
    .setPreference(DoubleIndentClassDeclaration, true)
    .setPreference(SpaceInsideParentheses, true)
    .setPreference(SpacesWithinPatternBinders, true)
    .setPreference(SpacesAroundMultiImports, true)
    .setPreference(DanglingCloseParenthesis, Force)
    .setPreference(CompactControlReadability, true)
    .setPreference(PlaceScaladocAsterisksBeneathSecondAsterisk, true)
)

// Code Quality

wartremoverExcluded ++= (sourceDirectory.value ** "*old*" ** "*.scala").get
wartremoverErrors in (Compile, compile) ++= Seq(
  Wart.Any2StringAdd,
  Wart.AsInstanceOf,
  Wart.EitherProjectionPartial,
  Wart.IsInstanceOf,
  Wart.ListOps,
  Wart.Nothing,
  Wart.Null,
  Wart.OptionPartial,
  Wart.Product,
  Wart.Serializable
)
wartremoverWarnings in (Compile, compile) ++= Seq(
  Wart.Any2StringAdd,
  Wart.EitherProjectionPartial,
  Wart.Enumeration,
  Wart.FinalCaseClass,
  Wart.JavaConversions,
  Wart.MutableDataStructures,
  Wart.Option2Iterable,
  Wart.Serializable,
  Wart.ToString,
  Wart.TryPartial,
  Wart.Var
)

coverageMinimum := 75
coverageFailOnMinimum := true

cpdSettings

com.markatta.sbttaglist.TagListPlugin.tagListSettings
