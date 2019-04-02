package libyaml

import scalanative.native._
import scalanative.native.stdio._

@link("yaml")
@extern
object clib {
  // const char *yaml_get_version_string(void);
  def yaml_get_version_string(): Ptr[CChar] = extern

  // void yaml_get_version(int *major, int *minor, int *patch);
  def yaml_get_version(major: Ptr[CInt],
                       minor: Ptr[CInt],
                       patch: Ptr[CInt]): Unit = extern

  //
  // Basic Types
  //

  type yaml_char_t = CUnsignedChar

  type yaml_version_directive_s = CStruct2[CInt, CInt]
  type yaml_version_directive_t = yaml_version_directive_s

  type yaml_tag_directive_s = CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]
  type yaml_tag_directive_t = yaml_tag_directive_s

  type yaml_encoding_t = Encoding

  // yaml_break_t

  // yaml_error_type_t

  type yaml_mark_s = CStruct3[CSize, CSize, CSize]
  type yaml_mark_t = yaml_mark_s

  //
  // Node Styles
  //

  type yaml_scalar_style_t = ScalarStyle

  type yaml_sequence_style_t = SequenceStyle

  type yaml_mapping_style_t = MappingStyle

  //
  // Tokens
  //

  type yaml_token_type_t = TokenType

  // yaml_token_t
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
  type yaml_token_t = yaml_token_s

  // void yaml_token_delete(yaml_token_t *token)
  def yaml_token_delete(token: Ptr[yaml_token_t]): Unit = extern

  //
  // Events
  //

  type yaml_event_type_t = EventType

  type yaml_event_tag_directives =
    CStruct2[Ptr[yaml_tag_directive_t], Ptr[yaml_tag_directive_t]]

  // yaml_event_s
  type yaml_event_stream_start = CStruct1[yaml_encoding_t]
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
  type yaml_event_sequence_start =
    CStruct4[Ptr[yaml_char_t], Ptr[yaml_char_t], CInt, yaml_sequence_style_t]
  type yaml_event_mapping_start =
    CStruct4[Ptr[yaml_char_t], Ptr[yaml_char_t], CInt, yaml_mapping_style_t]
  type yaml_event_data_u = CArray[Byte, Nat.Digit[Nat._4, Nat._8]]
  type yaml_event_s =
    CStruct4[yaml_event_type_t, yaml_event_data_u, yaml_mark_t, yaml_mark_t]
  type yaml_event_t = yaml_event_s

  // int yaml_stream_start_event_initialize(yaml event_t *event, yaml_encoding_t encoding);

  // int yaml_stream_end_event_initialize(yaml_event_t *event);

  // int yaml_document_start_event_initialize(yaml_event_t *event,
  //         yaml_version_directive_t *version_directive,
  //         yaml_tag_directive_t *tag_directives_start,
  //         yaml_tag_directive_t *tag_directives_end,
  //         int implicit);

  // int yaml_document_end_event_initialize(yaml_event_t *event, int implicit);

  // int yaml_alias_event_initialize(yaml_event_t *event, yaml_char_t *anchor);

  // int yaml_scalar_event_initialize(yaml_event_t *event,
  //         yaml_char_t *anchor, yaml_char_t *tag,
  //         yaml_char_t *value, int length,
  //         int plain_implicit, int quoted_implicit,
  //         yaml_scalar_style_t style);

  // int yaml_sequence_start_event_initialize(yaml_event_t *event,
  //         yaml_char_t *anchor, yaml_char_t *tag, int implicit,
  //         yaml_sequence_style_t style);

  // int yaml_sequence_end_event_initialize(yaml_event_t *event);

  // int yaml_mapping_start_event_initialize(yaml_event_t *event,
  //         yaml_char_t *anchor, yaml_char_t *tag, int implicit,
  //         yaml_mapping_style_t style);

  // int yaml_mapping_end_event_initialize(yaml_event_t *event);

  // void yaml_event_delete(yaml_event_t *event)
  def yaml_event_delete(event: Ptr[yaml_event_t]): Unit = extern

  //
  // Nodes
  //

  type yaml_node_type_t = NodeType

  type yaml_node_item_t = CInt

  type yaml_node_pair_s = CStruct2[CInt, CInt]
  type yaml_node_pair_t = yaml_node_pair_s

  // yaml_node_s
  type yaml_node_scalar =
    CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]
  type yaml_node_sequence_items = CStruct3[Ptr[yaml_node_item_t],
                                           Ptr[yaml_node_item_t],
                                           Ptr[yaml_node_item_t]]
  type yaml_node_sequence =
    CStruct2[yaml_node_sequence_items, yaml_sequence_style_t]
  type yaml_node_mapping_pairs = CStruct3[Ptr[yaml_node_pair_t],
                                          Ptr[yaml_node_pair_t],
                                          Ptr[yaml_node_pair_t]]
  type yaml_node_mapping =
    CStruct2[yaml_node_mapping_pairs, yaml_mapping_style_t]
  type yaml_node_data_u = CArray[Byte, Nat.Digit[Nat._3, Nat._2]]
  type yaml_node_s = CStruct5[yaml_node_type_t,
                              Ptr[yaml_char_t],
                              yaml_node_data_u,
                              yaml_mark_t,
                              yaml_mark_t]
  type yaml_node_t = yaml_node_s

  // yaml_document_s
  type yaml_document_nodes =
    CStruct3[Ptr[yaml_node_t], Ptr[yaml_node_t], Ptr[yaml_node_t]]
  type yaml_document_tag_directives =
    CStruct2[Ptr[yaml_tag_directive_t], Ptr[yaml_tag_directive_t]]
  type yaml_document_s = CStruct7[yaml_document_nodes,
                                  Ptr[yaml_version_directive_t],
                                  yaml_document_tag_directives,
                                  CInt,
                                  CInt,
                                  yaml_mark_t,
                                  yaml_mark_t]
  type yaml_document_t = yaml_document_s

  // int yaml_document_initialize(yaml_document_t *document,
  //         yaml_version_directive_t *version_directive,
  //         yaml_tag_directive_t *tag_directives_start,
  //         yaml_tag_directive_t *tag_directives_end,
  //         int start_implicit, int end_implicit);

  // void yaml_document_delete(yaml_document_t *document);

  // yaml_node_t *yaml_document_get_node(yaml_document_t *document, int index);

  // yaml_node_t *yaml_document_get_root_node(yaml_document_node *document);

  // int yaml_document_add_scalar(yaml_document_t *document,
  //         yaml_char_t *tag, yaml_char_t *value, int length,
  //         yaml_scalar_style_t style);

  // int yaml_document_add_sequence(yaml_document_t *document,
  //         yaml_char_t *tag, yaml_sequence_style_t style);

  // int yaml_document_add_mapping(yaml_document_t *document,
  //         yaml_char_t *tag, yaml_mapping_style_t style);

  // int yaml_document_append_sequence_item(yaml_document_t *document,
  //         int sequence, int item);

  // int yaml_document_append_mapping_pair(yaml_document_ *document,
  //         int mapping, int key, int value);

  //
  // Parser Definitions
  //

  type yaml_read_handler_t =
    CFunctionPtr4[Ptr[Unit], Ptr[CUnsignedChar], CSize, Ptr[CSize], CInt]

  type yaml_parser_t = extern

  // int yaml_parser_initialize(yaml_parser_t *)
  def yaml_parser_initialize(parser: Ptr[yaml_parser_t]): CInt = extern

  // void yaml_parser_delete(yaml_parser_t *)
  def yaml_parser_delete(parser: Ptr[yaml_parser_t]): Unit = extern

  // void yaml_parser_set_input_string(yaml_parser_t *parser, const unsigned char *input, size_t size);

  // void yaml_parser_set_input_file(yaml_parser_t *parser, FILE *file)
  def yaml_parser_set_input_file(parser: Ptr[yaml_parser_t],
                                 file: Ptr[FILE]): Unit = extern

  // void yaml_parser_set_input(yaml_parser_t *parser, yaml_read_handler_t *handler, void *data);

  // void yaml_parser_set_encoding(yaml_parser_t *parser, yaml_encoding_t encoding);

  // int yaml_parser_scan(yaml_parser_t *parser, yaml_token_t *token)
  def yaml_parser_scan(parser: Ptr[yaml_parser_t],
                       token: Ptr[yaml_token_t]): CInt = extern

  // int yaml_parser_parse(yaml_parser_t *parser, yaml_event_t *event)
  def yaml_parser_parse(parser: Ptr[yaml_parser_t],
                        event: Ptr[yaml_event_t]): CInt = extern

  // int yaml_parser_load(yaml_parser_t *parser, yaml_document_t *document);

  //
  // Emitter Definitions
  //

  type yaml_write_handler_t =
    CFunctionPtr2[Ptr[Unit], Ptr[CUnsignedChar], CSize]

  type yaml_emitter_t = extern

  // int yaml_emitter_initialize(yaml_emitter_t *emitter);

  // void yaml_emitter_delete(yaml_emitter_t *emitter);

  // void yaml_emitter_set_output_string(yaml_emitter_t *emitter,
  //         unsigned char *output, size_t size, size_t *size_written);

  // void yaml_emitter_set_output_file(yaml_emitter_t *emitter, FILE *file);

  // void yaml_emitter_set_output(yaml_emitter_t *emitter,
  //         yaml_write_handler_t *handler, void *data);

  // void yaml_emitter_set_encoding(yaml_emitter_t *emitter, yaml_encoding_t encoding);

  // void yaml_emitter_set_canonical(yaml_emitter_t *emitter, int canonical);

  // void yaml_emitter_set_indent(yaml_emitter_t *emitter, int indent);

  // void yaml_emitter_set_width(yaml_emitter_t *emitter, int width);

  // void yaml_emitter_set_unicode(yaml_emitter_t *emitter, int unicode);

  // void yaml_emitter_set_break(yaml_emitter_t *emitter, yaml_break_t line_break);

  // int yaml_emitter_emit(yaml_emitter_t *emitter, yaml_event_t *event);

  // int yaml_emitter_open(yaml_emitter_t *emitter);

  // int yaml_emitter_close(yaml_emitter_t *emitter);

  // int yaml_emitter_dump(yaml_emitter_t *emitter, yaml_document_t *document);

  // int yaml_emitter_flush(yaml_emitter_t *emitter);
}
