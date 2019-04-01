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
  type yaml_event_type_t = EventType
  type yaml_event_t = yaml_event_s

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

  // yaml_event_s
  type yaml_event_stream_start = CStruct1[yaml_encoding_t]

  type yaml_tag_directive_s = CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]
  type yaml_tag_directive_t = yaml_tag_directive_s
  type yaml_event_tag_directives =
    CStruct2[Ptr[yaml_tag_directive_t], Ptr[yaml_tag_directive_t]]
  type yaml_version_directive_s = CStruct2[CInt, CInt]
  type yaml_version_directive_t = yaml_version_directive_s
  type yaml_event_document_start =
    CStruct3[Ptr[yaml_version_directive_t], yaml_event_tag_directives, CInt]

  type yaml_event_document_end = CStruct1[CInt]

  type yaml_event_alias = CStruct1[Ptr[yaml_char_t]]

  type yaml_event_scalar = CStruct7[Ptr[yaml_char_t],
                                    Ptr[yaml_char_t],
                                    Ptr[yaml_char_t],
                                    CSize,
                                    CInt,
                                    CInt,
                                    yaml_scalar_style_t]

  type yaml_sequence_style_t = SequenceStyle
  type yaml_event_sequence_start =
    CStruct4[Ptr[yaml_char_t], Ptr[yaml_char_t], CInt, yaml_sequence_style_t]

  type yaml_mapping_style_t = MappingStyle
  type yaml_event_mapping_start =
    CStruct4[Ptr[yaml_char_t], Ptr[yaml_char_t], CInt, yaml_mapping_style_t]

  type yaml_event_data_u = CArray[Byte, Nat.Digit[Nat._4, Nat._8]]
  type yaml_event_s =
    CStruct4[yaml_event_type_t, yaml_event_data_u, yaml_mark_t, yaml_mark_t]

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

  // int yaml_parser_parse(yaml_parser_t *parser, yaml_event_t *event)
  def yaml_parser_parse(parser: Ptr[yaml_parser_t],
                        event: Ptr[yaml_event_t]): CInt = extern

  // void yaml_event_delete(yaml_event_t *event)
  def yaml_event_delete(event: Ptr[yaml_event_t]): Unit = extern
}
