package libyaml

import libyaml.clib._
import libyaml.clib.implicits._
import minitest._

import scalanative.native._
import scalanative.native.stdio._

/**
  * Run the second sample code in https://www.wpsoftware.net/andrew/pages/libyaml.html
  */
object Sample2 extends SimpleTestSuite {

  test("should run the second sample code") {
    Zone { implicit zone =>
      val fh = fopen(toCString("sample/config/public.yaml"), toCString("r"))
      val parser = LibYaml.Parser()
      val token = LibYaml.Token()

      // show version
      printf(toCString("%s\n"), yaml_get_version_string())

      val major = alloc[CInt]
      val minor = alloc[CInt]
      val patch = alloc[CInt]
      yaml_get_version(major, minor, patch)
      printf(toCString("%d.%d.%d\n"), !major, !minor, !patch)

      // Initialize parser
      if (yaml_parser_initialize(parser) == 0) {
        fail("Failed to initialize parser!\n")
      }

      // Check null pointer like this?
      // ref. https://github.com/scala-native/scala-native/pull/248
      if (fh == null) {
        fail("Failed to open file!\n")
      }

      // Set input file
      yaml_parser_set_input_file(parser, fh)

      // CODE HERE
      do {
        yaml_parser_scan(parser, token)
        token.typ match {
          case TokenType.StreamStartToken =>
            puts(toCString("STREAM START"))
          case TokenType.StreamEndToken =>
            puts(toCString("STREAM END"))
          case TokenType.KeyToken =>
            printf(toCString("(Key Token)    "))
          case TokenType.ValueToken =>
            printf(toCString("(Value Token)  "))
          case TokenType.BlockSequenceStartToken =>
            puts(toCString("<b>Start Block (Sequence)</b>"))
          case TokenType.BlockEntryToken =>
            puts(toCString("<b>Start Block (Entry)</b>"))
          case TokenType.BlockEndToken =>
            puts(toCString("<b>End block</b>"))
          case TokenType.BlockMappingStartToken =>
            puts(toCString("[Block mapping]"))
          case TokenType.ScalarToken =>
            printf(toCString("scalar %s \n"), token.data.scalar.value)
          case _ =>
            printf(toCString("Got token of type %d\n"), token.typ)
        }
        if (!token._1 != TokenType.StreamEndToken) {
          yaml_token_delete(token)
        }
      } while (!token._1 != TokenType.StreamEndToken)
      yaml_token_delete(token)

      // Cleanup
      yaml_parser_delete(parser)
      fclose(fh)

      ()
    }
  }

}
