package example

import libyaml.clib._
import libyaml.LibYaml

import scalanative.native._
import scalanative.native.stdio._

object Main {
  def main(args: Array[String]): Unit = {
    Zone { implicit zone =>
      val fh = fopen(toCString("sample/config/public.yaml"), toCString("r"))
      val parser = LibYaml.Parser()

      // Initialize parser
      if (yaml_parser_initialize(parser) == 0) {
        fputs(toCString("Failed to initialize parser!\n"), stderr)
      }

      // Check null pointer like this?
      // ref. https://github.com/scala-native/scala-native/pull/248
      if (fh != null) {
        fputs(toCString("Failed to open file!\n"), stderr)
      }

      // Set input file
      yaml_parser_set_input_file(parser, fh)

      // CODE HERE

      // Cleanup
      yaml_parser_delete(parser)
      fclose(fh)
    }
  }
}
