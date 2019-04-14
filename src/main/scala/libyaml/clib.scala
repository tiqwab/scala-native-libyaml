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

  type yaml_break_t = Break

  type yaml_error_type_t = ErrorType

  type yaml_mark_s = CStruct3[CSize, CSize, CSize]
  type yaml_mark_t = yaml_mark_s

  //
  // Common Types
  //
  // this is not in the original definition.
  // shared with yaml_event_tag_directives and yaml_document_tag_directives
  // to define implicit class commonly
  //

  type yaml_tag_directives_t =
    CStruct2[Ptr[yaml_tag_directive_t], Ptr[yaml_tag_directive_t]]

  //
  // Node Styles
  //

  type yaml_scalar_style_t = ScalarStyle

  type yaml_sequence_style_t = SequenceStyle

  type yaml_mapping_style_t = MappingStyle

  //
  // Tokens
  //

  object Token {
    sealed trait StreamStart
    sealed trait Alias
    sealed trait Anchor
    sealed trait Tag
    sealed trait Scalar
    sealed trait TagDirective
    sealed trait VersionDirective
    sealed trait Data
  }

  type yaml_token_type_t = TokenType

  // yaml_token_t
  type yaml_token_stream_start =
    CStruct1[yaml_encoding_t] @@ Token.StreamStart
  type yaml_token_alias =
    CStruct1[Ptr[yaml_char_t]] @@ Token.Alias
  type yaml_token_anchor =
    CStruct1[Ptr[yaml_char_t]] @@ Token.Anchor
  type yaml_token_tag =
    CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]] @@ Token.Tag

  type yaml_token_scalar =
    CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t] @@ Token.Scalar

  type yaml_token_version_directive =
    CStruct2[CInt, CInt] @@ Token.VersionDirective

  type yaml_token_tag_directive =
    CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]] @@ Token.TagDirective

  type yaml_token_data_u =
    CArray[Byte, Nat.Digit[Nat._2, Nat._4]] @@ Token.Data
  type yaml_token_s =
    CStruct4[yaml_token_type_t, yaml_token_data_u, yaml_mark_t, yaml_mark_t]
  type yaml_token_t = yaml_token_s

  // void yaml_token_delete(yaml_token_t *token)
  def yaml_token_delete(token: Ptr[yaml_token_t]): Unit = extern

  //
  // Events
  //

  sealed trait Event
  object Event {
    sealed trait StreamStart
    sealed trait DocumentStart
    sealed trait DocumentEnd
    sealed trait Alias
    sealed trait Scalar
    sealed trait SequenceStart
    sealed trait MappingStart
    sealed trait Data
  }

  type yaml_event_type_t = EventType

  type yaml_event_tag_directives = yaml_tag_directives_t

  // yaml_event_s
  type yaml_event_stream_start =
    CStruct1[yaml_encoding_t] @@ Event.StreamStart
  type yaml_event_document_start =
    CStruct3[Ptr[yaml_version_directive_t], yaml_event_tag_directives, CInt] @@ Event.DocumentStart
  type yaml_event_document_end =
    CStruct1[CInt] @@ Event.DocumentEnd
  type yaml_event_alias =
    CStruct1[Ptr[yaml_char_t]] @@ Event.Alias
  type yaml_event_scalar = CStruct7[Ptr[yaml_char_t],
                                    Ptr[yaml_char_t],
                                    Ptr[yaml_char_t],
                                    CSize,
                                    CInt,
                                    CInt,
                                    yaml_scalar_style_t] @@ Event.Scalar
  type yaml_event_sequence_start =
    CStruct4[Ptr[yaml_char_t], Ptr[yaml_char_t], CInt, yaml_sequence_style_t] @@ Event.SequenceStart
  type yaml_event_mapping_start =
    CStruct4[Ptr[yaml_char_t], Ptr[yaml_char_t], CInt, yaml_mapping_style_t] @@ Event.MappingStart
  type yaml_event_data_u =
    CArray[Byte, Nat.Digit[Nat._4, Nat._8]] @@ Event.Data
  type yaml_event_s =
    CStruct4[yaml_event_type_t, yaml_event_data_u, yaml_mark_t, yaml_mark_t]
  type yaml_event_t = yaml_event_s

  // int yaml_stream_start_event_initialize(yaml event_t *event, yaml_encoding_t encoding);
  def yaml_stream_start_event_initialize(event: Ptr[yaml_event_t],
                                         encoding: yaml_encoding_t): CInt =
    extern

  // int yaml_stream_end_event_initialize(yaml_event_t *event);
  def yaml_stream_end_event_initialize(event: Ptr[yaml_event_t]): CInt = extern

  // int yaml_document_start_event_initialize(yaml_event_t *event,
  //         yaml_version_directive_t *version_directive,
  //         yaml_tag_directive_t *tag_directives_start,
  //         yaml_tag_directive_t *tag_directives_end,
  //         int implicit);
  def yaml_document_start_event_initialize(
      event: Ptr[yaml_event_t],
      version_directive: Ptr[yaml_version_directive_t],
      tag_directives_start: Ptr[yaml_tag_directive_t],
      tag_directives_end: Ptr[yaml_tag_directive_t],
      _implicit: CInt): CInt = extern

  // int yaml_document_end_event_initialize(yaml_event_t *event, int implicit);
  def yaml_document_end_event_initialize(event: Ptr[yaml_event_t],
                                         _implicit: CInt): CInt = extern

  // int yaml_alias_event_initialize(yaml_event_t *event, yaml_char_t *anchor);
  def yaml_alias_event_initialize(event: Ptr[yaml_event_t],
                                  anchor: Ptr[yaml_char_t]): CInt = extern

  // int yaml_scalar_event_initialize(yaml_event_t *event,
  //         yaml_char_t *anchor, yaml_char_t *tag,
  //         yaml_char_t *value, int length,
  //         int plain_implicit, int quoted_implicit,
  //         yaml_scalar_style_t style);
  def yaml_scalar_event_initialize(event: Ptr[yaml_event_t],
                                   anchor: Ptr[yaml_char_t],
                                   tag: Ptr[yaml_char_t],
                                   value: Ptr[yaml_char_t],
                                   length: CInt,
                                   plain_implicit: CInt,
                                   quoted_implicit: CInt,
                                   style: yaml_scalar_style_t): CInt = extern

  // int yaml_sequence_start_event_initialize(yaml_event_t *event,
  //         yaml_char_t *anchor, yaml_char_t *tag, int implicit,
  //         yaml_sequence_style_t style);
  def yaml_sequence_start_event_initialize(event: Ptr[yaml_event_t],
                                           anchor: Ptr[yaml_char_t],
                                           tag: Ptr[yaml_char_t],
                                           _implicit: CInt,
                                           style: yaml_sequence_style_t): CInt =
    extern

  // int yaml_sequence_end_event_initialize(yaml_event_t *event);
  def yaml_sequence_end_event_initialize(event: Ptr[yaml_event_t]): CInt =
    extern

  // int yaml_mapping_start_event_initialize(yaml_event_t *event,
  //         yaml_char_t *anchor, yaml_char_t *tag, int implicit,
  //         yaml_mapping_style_t style);
  def yaml_mapping_start_event_initialize(event: Ptr[yaml_event_t],
                                          anchor: Ptr[yaml_char_t],
                                          tag: Ptr[yaml_char_t],
                                          _implicit: CInt,
                                          style: yaml_mapping_style_t): CInt =
    extern

  // int yaml_mapping_end_event_initialize(yaml_event_t *event);
  def yaml_mapping_end_event_initialize(event: Ptr[yaml_event_t]): CInt = extern

  // void yaml_event_delete(yaml_event_t *event)
  def yaml_event_delete(event: Ptr[yaml_event_t]): Unit = extern

  //
  // Nodes
  //

  object Node {
    sealed trait Scalar
    sealed trait SequenceItems
    sealed trait Sequence
    sealed trait MappingPairs
    sealed trait Mapping
    sealed trait Data
  }

  type yaml_node_type_t = NodeType

  type yaml_node_item_t = CInt

  type yaml_node_pair_s =
    CStruct2[CInt, CInt]
  type yaml_node_pair_t = yaml_node_pair_s

  // yaml_node_s
  type yaml_node_scalar =
    CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t] @@ Node.Scalar

  type yaml_node_sequence_items =
    CStruct3[Ptr[yaml_node_item_t],
             Ptr[yaml_node_item_t],
             Ptr[yaml_node_item_t]] @@ Node.SequenceItems
  type yaml_node_sequence =
    CStruct2[yaml_node_sequence_items, yaml_sequence_style_t] @@ Node.Sequence
  type yaml_node_mapping_pairs =
    CStruct3[Ptr[yaml_node_pair_t],
             Ptr[yaml_node_pair_t],
             Ptr[yaml_node_pair_t]] @@ Node.MappingPairs
  type yaml_node_mapping =
    CStruct2[yaml_node_mapping_pairs, yaml_mapping_style_t] @@ Node.Mapping
  type yaml_node_data_u =
    CArray[Byte, Nat.Digit[Nat._3, Nat._2]] // @@ Node.Data // FIXME: failed to parse NodeType?
  type yaml_node_s = CStruct5[yaml_node_type_t,
                              Ptr[yaml_char_t],
                              yaml_node_data_u,
                              yaml_mark_t,
                              yaml_mark_t]
  type yaml_node_t = yaml_node_s

  // yaml_document_s
  type yaml_document_nodes =
    CStruct3[Ptr[yaml_node_t], Ptr[yaml_node_t], Ptr[yaml_node_t]]
  type yaml_document_tag_directives = yaml_tag_directives_t
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
  def yaml_document_initialize(document: Ptr[yaml_document_t],
                               version_directive: Ptr[yaml_version_directive_t],
                               tag_directive_start: Ptr[yaml_tag_directive_t],
                               tag_directive_end: Ptr[yaml_tag_directive_t],
                               start_implicit: CInt,
                               end_implicit: CInt): CInt = extern

  // void yaml_document_delete(yaml_document_t *document);
  def yaml_document_delete(document: Ptr[yaml_document_t]): Unit = extern

  // yaml_node_t *yaml_document_get_node(yaml_document_t *document, int index);
  def yaml_document_get_node(document: Ptr[yaml_document_t],
                             index: CInt): Ptr[yaml_node_t] = extern

  // yaml_node_t *yaml_document_get_root_node(yaml_document_node *document);
  def yaml_document_get_root_node(
      document: Ptr[yaml_document_t]): Ptr[yaml_node_t] = extern

  // int yaml_document_add_scalar(yaml_document_t *document,
  //         yaml_char_t *tag, yaml_char_t *value, int length,
  //         yaml_scalar_style_t style);
  def yaml_document_add_scalar(document: Ptr[yaml_document_t],
                               tag: Ptr[yaml_char_t],
                               value: Ptr[yaml_char_t],
                               length: CInt,
                               style: yaml_scalar_style_t): CInt = extern

  // int yaml_document_add_sequence(yaml_document_t *document,
  //         yaml_char_t *tag, yaml_sequence_style_t style);
  def yaml_document_add_sequence(document: Ptr[yaml_document_t],
                                 tag: Ptr[yaml_char_t],
                                 style: yaml_sequence_style_t): CInt = extern

  // int yaml_document_add_mapping(yaml_document_t *document,
  //         yaml_char_t *tag, yaml_mapping_style_t style);
  def yaml_document_add_mapping(document: Ptr[yaml_document_t],
                                tag: Ptr[yaml_char_t],
                                style: yaml_mapping_style_t): CInt = extern

  // int yaml_document_append_sequence_item(yaml_document_t *document,
  //         int sequence, int item);
  def yaml_document_append_sequence_item(document: Ptr[yaml_document_t],
                                         sequence: CInt,
                                         item: CInt): CInt = extern

  // int yaml_document_append_mapping_pair(yaml_document_t *document,
  //         int mapping, int key, int value);
  def yaml_document_append_mapping_pair(document: Ptr[yaml_document_t],
                                        mapping: CInt,
                                        key: CInt,
                                        value: CInt): CInt = extern

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
  def yaml_parser_set_input_string(parser: Ptr[yaml_parser_t],
                                   input: Ptr[CUnsignedChar],
                                   size: CSize): Unit = extern

  // void yaml_parser_set_input_file(yaml_parser_t *parser, FILE *file)
  def yaml_parser_set_input_file(parser: Ptr[yaml_parser_t],
                                 file: Ptr[FILE]): Unit = extern

  // void yaml_parser_set_input(yaml_parser_t *parser, yaml_read_handler_t *handler, void *data);
  def yaml_parser_set_input(parser: Ptr[yaml_parser_t],
                            handler: Ptr[yaml_read_handler_t],
                            data: Ptr[Byte]): Unit = extern

  // void yaml_parser_set_encoding(yaml_parser_t *parser, yaml_encoding_t encoding);
  def yaml_parser_set_encoding(parser: Ptr[yaml_parser_t],
                               encoding: yaml_encoding_t): Unit = extern

  // int yaml_parser_scan(yaml_parser_t *parser, yaml_token_t *token)
  def yaml_parser_scan(parser: Ptr[yaml_parser_t],
                       token: Ptr[yaml_token_t]): CInt = extern

  // int yaml_parser_parse(yaml_parser_t *parser, yaml_event_t *event)
  def yaml_parser_parse(parser: Ptr[yaml_parser_t],
                        event: Ptr[yaml_event_t]): CInt = extern

  // int yaml_parser_load(yaml_parser_t *parser, yaml_document_t *document);
  def yaml_parser_load(parser: Ptr[yaml_parser_t],
                       document: Ptr[yaml_document_t]): CInt = extern

  //
  // Emitter Definitions
  //

  type yaml_write_handler_t =
    CFunctionPtr2[Ptr[Unit], Ptr[CUnsignedChar], CSize]

  type yaml_emitter_t = extern

  // int yaml_emitter_initialize(yaml_emitter_t *emitter);
  def yaml_emitter_initialize(emitter: Ptr[yaml_emitter_t]): CInt = extern

  // void yaml_emitter_delete(yaml_emitter_t *emitter);
  def yaml_emitter_delete(emitter: Ptr[yaml_emitter_t]): Unit = extern

  // void yaml_emitter_set_output_string(yaml_emitter_t *emitter,
  //         unsigned char *output, size_t size, size_t *size_written);
  def yaml_emitter_set_output_string(emitter: Ptr[yaml_emitter_t],
                                     output: Ptr[CUnsignedChar],
                                     size: CSize,
                                     written: Ptr[CSize]): Unit = extern

  // void yaml_emitter_set_output_file(yaml_emitter_t *emitter, FILE *file);
  def yaml_emitter_set_output_file(emitter: Ptr[yaml_emitter_t],
                                   file: Ptr[stdio.FILE]): Unit = extern

  // void yaml_emitter_set_output(yaml_emitter_t *emitter,
  //         yaml_write_handler_t *handler, void *data);
  def yaml_emitter_set_output(emitter: Ptr[yaml_emitter_t],
                              handler: Ptr[yaml_write_handler_t],
                              data: Ptr[Byte]): Unit = extern

  // void yaml_emitter_set_encoding(yaml_emitter_t *emitter, yaml_encoding_t encoding);
  def yaml_emitter_set_encoding(emitter: Ptr[yaml_emitter_t],
                                encoding: yaml_encoding_t): Unit = extern

  // void yaml_emitter_set_canonical(yaml_emitter_t *emitter, int canonical);
  def yaml_emitter_set_canonical(emitter: Ptr[yaml_emitter_t],
                                 canonical: CInt): Unit = extern

  // void yaml_emitter_set_indent(yaml_emitter_t *emitter, int indent);
  def yaml_emitter_set_indent(emitter: Ptr[yaml_emitter_t],
                              indent: CInt): Unit = extern

  // void yaml_emitter_set_width(yaml_emitter_t *emitter, int width);
  def yaml_emitter_set_width(emitter: Ptr[yaml_emitter_t], width: CInt): Unit =
    extern

  // void yaml_emitter_set_unicode(yaml_emitter_t *emitter, int unicode);
  def yaml_emitter_set_unicode(emitter: Ptr[yaml_emitter_t],
                               unicode: CInt): Unit = extern

  // void yaml_emitter_set_break(yaml_emitter_t *emitter, yaml_break_t line_break);
  def yaml_emitter_set_break(emitter: Ptr[yaml_emitter_t],
                             line_break: yaml_break_t): Unit = extern

  // int yaml_emitter_emit(yaml_emitter_t *emitter, yaml_event_t *event);
  def yaml_emitter_emit(emitter: Ptr[yaml_emitter_t],
                        event: Ptr[yaml_event_t]): CInt = extern

  // int yaml_emitter_open(yaml_emitter_t *emitter);
  def yaml_emitter_open(emitter: Ptr[yaml_emitter_t]): CInt = extern

  // int yaml_emitter_close(yaml_emitter_t *emitter);
  def yaml_emitter_close(emitter: Ptr[yaml_emitter_t]): CInt = extern

  // int yaml_emitter_dump(yaml_emitter_t *emitter, yaml_document_t *document);
  def yaml_emitter_dump(emitter: Ptr[yaml_emitter_t],
                        document: Ptr[yaml_document_t]): CInt = extern

  // int yaml_emitter_flush(yaml_emitter_t *emitter);
  def yaml_emitter_flush(emitter: Ptr[yaml_emitter_t]): CInt = extern

  object implicits {
    implicit class yaml_version_directive_t_ops(
        p: Ptr[yaml_version_directive_t]) {
      def major: CInt = !p._1
      def minor: CInt = !p._2
    }

    implicit class yaml_tag_directive_t_ops(p: Ptr[yaml_tag_directive_t]) {
      def handle: Ptr[yaml_char_t] = !p._1
      def prefix: Ptr[yaml_char_t] = !p._2
    }

    implicit class yaml_tag_directives_t_ops(p: Ptr[yaml_tag_directives_t]) {
      def start: Ptr[yaml_tag_directive_t] = !p._1
      def end: Ptr[yaml_tag_directive_t] = !p._2
    }

    //
    // yaml_token_t
    //

    implicit class yaml_token_stream_start_ops(
        p: Ptr[yaml_token_stream_start]) {
      def encoding: Encoding = !p.cast[Ptr[CStruct1[yaml_encoding_t]]]._1
    }

    implicit class yaml_token_alias_ops(p: Ptr[yaml_token_alias]) {
      def value: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct1[Ptr[yaml_char_t]]]]._1
    }

    implicit class yaml_token_anchor_ops(p: Ptr[yaml_token_anchor]) {
      def value: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct1[Ptr[yaml_char_t]]]]._1
    }

    implicit class yaml_token_tag_ops(p: Ptr[yaml_token_tag]) {
      def handle: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]]]._1
      def suffix: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]]]._2
    }

    implicit class yaml_token_scalar_ops(p: Ptr[yaml_token_scalar]) {
      def value: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]]]._1
      def length: CSize =
        !p.cast[Ptr[CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]]]._2
      def style: ScalarStyle =
        !p.cast[Ptr[CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]]]._3
    }

    implicit class yaml_token_version_directive_ops(
        p: Ptr[yaml_token_version_directive]) {
      def major: CInt = !p.cast[Ptr[CStruct2[CInt, CInt]]]._1
      def minor: CInt = !p.cast[Ptr[CStruct2[CInt, CInt]]]._2
    }

    implicit class yaml_token_tag_directive_ops(
        p: Ptr[yaml_token_tag_directive]) {
      def handle: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]]]._1
      def prefix: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct2[Ptr[yaml_char_t], Ptr[yaml_char_t]]]]._2
    }

    implicit class yaml_token_data_u_ops(p: Ptr[yaml_token_data_u]) {
      def stream_start: Ptr[yaml_token_stream_start] =
        p.cast[Ptr[yaml_token_stream_start]]
      def alias: Ptr[yaml_token_alias] = p.cast[Ptr[yaml_token_alias]]
      def anchor: Ptr[yaml_token_anchor] = p.cast[Ptr[yaml_token_anchor]]
      def tag: Ptr[yaml_token_tag] = p.cast[Ptr[yaml_token_tag]]
      def scalar: Ptr[yaml_token_scalar] = p.cast[Ptr[yaml_token_scalar]]
      def version_directive: Ptr[yaml_token_version_directive] =
        p.cast[Ptr[yaml_token_version_directive]]
      def tag_directive: Ptr[yaml_token_tag_directive] =
        p.cast[Ptr[yaml_token_tag_directive]]
    }

    implicit class yaml_token_t_ops(p: Ptr[yaml_token_t]) {
      def typ: yaml_token_type_t = !p._1
      def data: Ptr[yaml_token_data_u] = p._2
      def start_mark: Ptr[yaml_mark_t] = p._3
      def end_mark: Ptr[yaml_mark_t] = p._4
    }

    //
    // yaml_event_t
    //

    implicit class yaml_event_stream_start_ops(
        p: Ptr[yaml_event_stream_start]) {
      def encoding: yaml_encoding_t = !p.cast[Ptr[CStruct1[yaml_encoding_t]]]._1
    }

    implicit class yaml_event_document_start_ops(
        p: Ptr[yaml_event_document_start]) {
      type T = Ptr[CStruct3[Ptr[yaml_version_directive_t],
                            yaml_event_tag_directives,
                            CInt]]

      def version_directive: Ptr[yaml_version_directive_t] = !p.cast[T]._1
      def tag_directives: Ptr[yaml_event_tag_directives] = p.cast[T]._2
      def _implicit: CInt = !p.cast[T]._3
    }

    implicit class yaml_event_document_end_ops(
        p: Ptr[yaml_event_document_end]) {
      def _implicit: CInt =
        !p.cast[Ptr[CStruct1[CInt]]]._1
    }

    implicit class yaml_event_alias_ops(p: Ptr[yaml_event_alias]) {
      def anchor: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct1[Ptr[yaml_char_t]]]]._1
    }

    implicit class yaml_event_scalar_ops(p: Ptr[yaml_event_scalar]) {
      type T = Ptr[
        CStruct7[Ptr[yaml_char_t],
                 Ptr[yaml_char_t],
                 Ptr[yaml_char_t],
                 CSize,
                 CInt,
                 CInt,
                 yaml_scalar_style_t]]

      def anchor: Ptr[yaml_char_t] = !p.cast[T]._1
      def tag: Ptr[yaml_char_t] = !p.cast[T]._2
      def value: Ptr[yaml_char_t] = !p.cast[T]._3
      def length: CSize = !p.cast[T]._4
      def plain_implicit: CInt = !p.cast[T]._5
      def quoted_implicit: CInt = !p.cast[T]._6
      def style: yaml_scalar_style_t = !p.cast[T]._7
    }

    implicit class yaml_event_sequence_start_ops(
        p: Ptr[yaml_event_sequence_start]) {
      type T = Ptr[CStruct4[Ptr[yaml_char_t],
                            Ptr[yaml_char_t],
                            CInt,
                            yaml_sequence_style_t]]

      def anchor: Ptr[yaml_char_t] = !p.cast[T]._1
      def tag: Ptr[yaml_char_t] = !p.cast[T]._2
      def _implicit: CInt = !p.cast[T]._3
      def style: yaml_sequence_style_t = !p.cast[T]._4
    }

    implicit class yaml_event_mapping_start_ops(
        p: Ptr[yaml_event_mapping_start]) {
      type T = Ptr[CStruct4[Ptr[yaml_char_t],
                            Ptr[yaml_char_t],
                            CInt,
                            yaml_mapping_style_t]]

      def anchor: Ptr[yaml_char_t] = !p.cast[T]._1
      def tag: Ptr[yaml_char_t] = !p.cast[T]._2
      def _implicit: CInt = !p.cast[T]._3
      def style: yaml_mapping_style_t = !p.cast[T]._4
    }

    implicit class yaml_event_data_u_ops(p: Ptr[yaml_event_data_u]) {
      def stream_start: Ptr[yaml_event_stream_start] =
        p.cast[Ptr[yaml_event_stream_start]]
      def document_start: Ptr[yaml_event_document_start] =
        p.cast[Ptr[yaml_event_document_start]]
      def document_end: Ptr[yaml_event_document_end] =
        p.cast[Ptr[yaml_event_document_end]]
      def alias: Ptr[yaml_event_alias] =
        p.cast[Ptr[yaml_event_alias]]
      def scalar: Ptr[yaml_event_scalar] =
        p.cast[Ptr[yaml_event_scalar]]
      def sequence_start: Ptr[yaml_event_sequence_start] =
        p.cast[Ptr[yaml_event_sequence_start]]
      def mapping_start: Ptr[yaml_event_mapping_start] =
        p.cast[Ptr[yaml_event_mapping_start]]
    }

    implicit class yaml_event_t_ops(p: Ptr[yaml_event_t]) {
      def typ: EventType = !p._1
      def data: Ptr[yaml_event_data_u] = p._2.cast[Ptr[yaml_event_data_u]]
      def start_mark: Ptr[yaml_mark_t] = p._3
      def end_mark: Ptr[yaml_mark_t] = p._4
    }

    //
    // yaml_node_t
    //

    implicit class yaml_node_scalar_ops(p: Ptr[yaml_node_scalar]) {
      def value: Ptr[yaml_char_t] =
        !p.cast[Ptr[CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]]]._1
      def length: CSize =
        !p.cast[Ptr[CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]]]._2
      def style: yaml_scalar_style_t =
        !p.cast[Ptr[CStruct3[Ptr[yaml_char_t], CSize, yaml_scalar_style_t]]]._3
    }

    implicit class yaml_node_sequence_items_ops(
        p: Ptr[yaml_node_sequence_items]) {
      type T = Ptr[CStruct3[Ptr[yaml_node_item_t],
                            Ptr[yaml_node_item_t],
                            Ptr[yaml_node_item_t]]]

      def start: Ptr[yaml_node_item_t] = !p.cast[T]._1
      def end: Ptr[yaml_node_item_t] = !p.cast[T]._2
      def top: Ptr[yaml_node_item_t] = !p.cast[T]._3
    }

    implicit class yaml_node_sequence_ops(p: Ptr[yaml_node_sequence]) {
      type T = Ptr[CStruct2[yaml_node_sequence_items, yaml_sequence_style_t]]

      def items: Ptr[yaml_node_sequence_items] = p.cast[T]._1
      def style: yaml_sequence_style_t = !p.cast[T]._2
    }

    implicit class yaml_node_pair_t_ops(p: Ptr[yaml_node_pair_t]) {
      def key: CInt = !p._1
      def value: CInt = !p._2
    }

    implicit class yaml_node_mapping_pairs_ops(
        p: Ptr[yaml_node_mapping_pairs]) {
      type T = Ptr[CStruct3[Ptr[yaml_node_pair_t],
                            Ptr[yaml_node_pair_t],
                            Ptr[yaml_node_pair_t]]]

      def start: Ptr[yaml_node_pair_t] = !p.cast[T]._1
      def end: Ptr[yaml_node_pair_t] = !p.cast[T]._2
      def top: Ptr[yaml_node_pair_t] = !p.cast[T]._3
    }

    implicit class yaml_node_mapping_ops(p: Ptr[yaml_node_mapping]) {
      type T =
        Ptr[CStruct2[yaml_node_mapping_pairs, yaml_mapping_style_t]]

      def pairs: Ptr[yaml_node_mapping_pairs] = p.cast[T]._1
      def style: yaml_mapping_style_t = !p.cast[T]._2
    }

    implicit class yaml_node_data_u_ops(p: Ptr[yaml_node_data_u]) {
      def scalar: Ptr[yaml_node_scalar] = p.cast[Ptr[yaml_node_scalar]]
      def sequence: Ptr[yaml_node_sequence] = p.cast[Ptr[yaml_node_sequence]]
      def mapping: Ptr[yaml_node_mapping] = p.cast[Ptr[yaml_node_mapping]]
    }

    implicit class yaml_node_t_ops(p: Ptr[yaml_node_t]) {
      def typ: NodeType = !p._1
      def tag: Ptr[yaml_char_t] = !p._2
      def data: Ptr[yaml_node_data_u] = p._3.cast[Ptr[yaml_node_data_u]]
      def start_mark: Ptr[yaml_mark_t] = p._4
      def end_mark: Ptr[yaml_mark_t] = p._5
    }

    //
    // yaml_document_t
    //

    implicit class yaml_document_nodes_ops(p: Ptr[yaml_document_nodes]) {
      def start: Ptr[yaml_node_t] = !p._1
      def end: Ptr[yaml_node_t] = !p._2
      def top: Ptr[yaml_node_t] = !p._3
    }

    implicit class yaml_document_t_ops(p: Ptr[yaml_document_t]) {
      def nodes: Ptr[yaml_document_nodes] = p._1
      def version_directive: Ptr[yaml_version_directive_t] = !p._2
      def tag_directives: Ptr[yaml_document_tag_directives] = p._3
      def start_implicit: CInt = !p._4
      def end_implicit: CInt = !p._5
      /*
      def start_mark: yaml_mark_t = !p._6
      def end_mark: yaml_mark_t = !p._7
     */
    }
  }
}
