package libyaml

import scalanative.native._
import scalanative.native.stdio._

@link("yaml")
@extern
object clib {
  type yaml_parser_t = extern
  type yaml_token_type_t = TokenType
  type yaml_token_t = yaml_token_s
  type yaml_mark_t = CStruct3[CSize, CSize, CSize]
  type yaml_encoding_t = Encoding
  type yaml_char_t = CUnsignedChar
  type yaml_scalar_style_t = ScalarStyle

  // yaml_token_s
  type yaml_token_stream_start = CStruct1[yaml_encoding_t]
  type yaml_token_alias = CStruct1[Ptr[yaml_char_t]]
  type yaml_token_anchor = CStruct1[Ptr[yaml_char_t]]
  type yaml_token_tag = CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]
  type yaml_token_scalar =
    CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]
  type yaml_token_version_directive = CStruct2[CInt, CInt]
  type yaml_token_tag_directive = CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]
  type yaml_token_data_u = CArray[Byte, Nat.Digit[Nat._2, Nat._4]]
  type yaml_token_s =
    CStruct4[yaml_token_type_t, yaml_token_data_u, yaml_mark_t, yaml_mark_t]

  // int yaml_parser_initialize(yaml_parser_t *)
  def yaml_parser_initialize(parser: Ptr[yaml_parser_t]): CInt = extern

  // void yaml_parser_delete(yaml_parser_t *)
  def yaml_parser_delete(parser: Ptr[yaml_parser_t]): Unit = extern

  // void yaml_parser_set_input_file(yaml_parser_t *parser, FILE *file)
  def yaml_parser_set_input_file(parser: Ptr[yaml_parser_t],
                                 file: Ptr[FILE]): Unit = extern

  // int yaml_parser_scan(yaml_parser_t *parser, yaml_token_t *token)
  def yaml_parser_scan(parser: Ptr[yaml_parser_t],
                       token: Ptr[yaml_token_t]): CInt = extern

  // void yaml_token_delete(yaml_token_t *token)
  def yaml_token_delete(token: Ptr[yaml_token_t]): Unit = extern
}
