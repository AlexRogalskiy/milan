package com.amazon.milan.compiler.scala

import com.amazon.milan.SemanticVersion
import com.amazon.milan.application.sinks.ConsoleDataSink
import com.amazon.milan.application.{Application, ApplicationConfiguration, ApplicationInstance}
import com.amazon.milan.compiler.scala.testing.IntRecord
import com.amazon.milan.graph.StreamCollection
import com.amazon.milan.lang._
import org.junit.Assert._
import org.junit.Test

import java.io.ByteArrayOutputStream


@Test
class TestScalaEventCompiler {
  @Test
  def test_ScalaEventCompiler_Compile_OutputsCode(): Unit = {
    val input = Stream.of[IntRecord].withId("input")
    val output = input.map(r => IntRecord(r.i + 1)).withId("output")

    val streams = StreamCollection.build(output)
    val app = new Application("app", streams, SemanticVersion.ZERO)
    val config = new ApplicationConfiguration()
    config.addSink(output, new ConsoleDataSink[IntRecord])
    val instance = new ApplicationInstance("instance", app, config)

    val params = List(("package", "testPackage"), ("class", "TestClass"))

    val compiler = new ScalaEventCompiler

    val outputStream = new ByteArrayOutputStream()
    compiler.compile(instance, params, outputStream)

    val classCode = outputStream.toString

    assertTrue(classCode.startsWith("package testPackage"))
    assertTrue(classCode.contains("TestClass"))
  }
}
