package libyaml

import libyaml.clib._
import minitest._

import scalanative.native._

object YamlParserLoadTest extends SimpleTestSuite {
  test("parse string input to document") {
    Zone { implicit z =>
      // Prepare input
      val inputStr =
        """
          |%YAML 1.1
          |---
          |name: Alice
          |favorites:
          |  - apple
          |  - banana
        """.stripMargin
      val input = toCString(inputStr).cast[Ptr[CUnsignedChar]]
      val size = inputStr.length

      // Initialize parser
      val parser = LibYaml.Parser()
      if (yaml_parser_initialize(parser) == 0) {
        fail("Failed to initialize parser!\n")
      }

      // Set input
      yaml_parser_set_encoding(parser, Encoding.UTF8)
      yaml_parser_set_input_string(parser, input, size)

      // Parse to document
      val document = LibYaml.Document()
      if (yaml_parser_load(parser, document) == 0) {
        fail("Failed to parse document!\n")
      }

      val major = !(!document._2)._1
      val minor = !(!document._2)._2
      assert(major == 1, "major version should be 1")
      assert(minor == 1, "minor version should be 1")

      yaml_document_delete(document)
      yaml_parser_delete(parser)
      ()
    }
  }
}
