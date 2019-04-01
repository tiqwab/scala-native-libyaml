package example

import libyaml.clib._
import libyaml.{LibYaml, TokenType}

import scalanative.native._
import scalanative.native.stdio._

object Main {
  def main(args: Array[String]): Unit = {
    Zone { implicit zone =>
      val fh = fopen(toCString("sample/config/public.yaml"), toCString("r"))
      val parser = LibYaml.Parser()
      val token = LibYaml.Token()

      printf(toCString("scalar %p\n"), CVararg(token))
      printf(toCString("%d\n"), sizeof[yaml_token_type_t])
      printf(toCString("%d\n"), sizeof[yaml_token_data_u])
      printf(toCString("%d\n"), sizeof[yaml_mark_t])
      printf(toCString("%d\n"), sizeof[yaml_token_scalar])
      printf(toCString("%d\n"), sizeof[yaml_token_s])

      // Initialize parser
      if (yaml_parser_initialize(parser) == 0) {
        fputs(toCString("Failed to initialize parser!\n"), stderr)
      }

      // Check null pointer like this?
      // ref. https://github.com/scala-native/scala-native/pull/248
      if (fh == null) {
        fputs(toCString("Failed to open file!\n"), stderr)
      }

      // Set input file
      yaml_parser_set_input_file(parser, fh)

      // CODE HERE
      do {
        yaml_parser_scan(parser, token)
        !token._1 match {
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
            printf(toCString("scalar %s \n"),
                   !token._2.cast[Ptr[yaml_token_scalar]]._1)
          case _ =>
            printf(toCString("Got token of type %d\n"), !token._1)
        }
        if (!token._1 != TokenType.StreamEndToken) {
          yaml_token_delete(token)
        }
      } while (!token._1 != TokenType.StreamEndToken)
      yaml_token_delete(token)

      // Cleanup
      yaml_parser_delete(parser)
      fclose(fh)
    }
  }
}
