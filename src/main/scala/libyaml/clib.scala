package libyaml

import scalanative.native._
import scalanative.native.stdio._

@link("yaml")
@extern
object clib {
  type yaml_parser_t = extern

  // int yaml_parser_initialize(yaml_parser_t *)
  def yaml_parser_initialize(parser: Ptr[yaml_parser_t]): CInt = extern

  // void yaml_parser_delete(yaml_parser_t *)
  def yaml_parser_delete(parser: Ptr[yaml_parser_t]): Unit = extern

  // void yaml_parser_set_input_file(yaml_parser_t *parser, FILE *file)
  def yaml_parser_set_input_file(parser: Ptr[yaml_parser_t],
                                 file: Ptr[FILE]): Unit = extern
}
