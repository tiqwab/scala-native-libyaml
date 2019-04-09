package libyaml

import libyaml.clib._
import libyaml.clib.implicits._
import minitest._

import scalanative.native._

object YamlParserLoadTest extends SimpleTestSuite {
  test("should parse string input to document") {
    Zone { implicit z =>
      // Prepare input
      val inputStr =
        """
          |%YAML 1.1
          |%TAG !yaml! tag:yaml.org,2002:
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

      // Check veresion directive
      val major = document.version_directive.major
      val minor = document.version_directive.minor
      assert(major == 1, "major version should be 1")
      assert(minor == 1, "minor version should be 1")

      // Check tag directive
      val start = document.tag_directives.start
      val end = document.tag_directives.end
      assert(end - start == 1,
             s"document should contain only one tag but ${end - start}")
      val handle = fromCString(start.handle.cast[CString])
      val prefix = fromCString(start.prefix.cast[CString])
      assert(handle == "!yaml!", s"unexpected handle: $handle")
      assert(prefix == "tag:yaml.org,2002:", s"unexpected prefix: $prefix")

      yaml_document_delete(document)
      yaml_parser_delete(parser)
      ()
    }
  }
}
