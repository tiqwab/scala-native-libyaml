package libyaml

import libyaml.clib._
import libyaml.clib.implicits._
import minitest._

import scalanative.native._

object EmitterTest extends SimpleTestSuite {

  val inputStr: String =
    """
      |%YAML 1.1
      |%TAG !yaml! tag:yaml.org,2002:
      |---
      |# name: Alice
      |# address:
      |#   city: Tokyo
      |#   country: Japan
      |# favorites:
      |#   - name: apple
      |#     reason: delicious
      |#   - name: banana
      |#     reason: yellow
    """.stripMargin

  val BufferSize = 65536
  val MaxEvents = 1024

  def copy_event(event_to: Ptr[yaml_event_t],
                 event_from: Ptr[yaml_event_t]): CInt = {
    event_from.typ match {
      case EventType.StreamStartEvent =>
        yaml_stream_start_event_initialize(
          event_to,
          event_from.data.stream_start.encoding)
      case EventType.StreamEndEvent =>
        yaml_stream_end_event_initialize(event_to)
      case EventType.DocumentStartEvent =>
        yaml_document_start_event_initialize(
          event_to,
          event_from.data.document_start.version_directive,
          event_from.data.document_start.tag_directives.start,
          event_from.data.document_start.tag_directives.end,
          event_from.data.document_start._implicit
        )
      case EventType.DocumentEndEvent =>
        yaml_document_end_event_initialize(
          event_to,
          event_from.data.document_end._implicit)
      case EventType.AliasEvent =>
        ???
      case EventType.ScalarEvent =>
        yaml_scalar_event_initialize(
          event_to,
          event_from.data.scalar.anchor,
          event_from.data.scalar.tag,
          event_from.data.scalar.value,
          event_from.data.scalar.length.toInt,
          event_from.data.scalar.plain_implicit,
          event_from.data.scalar.quoted_implicit,
          event_from.data.scalar.style
        )
      case EventType.SequenceStartEvent =>
        ???
      case EventType.SequenceEndEvent =>
        ???
      case EventType.MappingStartEvent =>
        ???
      case EventType.MappingEndEvent =>
        ???
      case _ =>
        1
    }
  }

  test("should emit events") {
    Zone { implicit z =>
      val input = toCString(inputStr).cast[Ptr[CUnsignedChar]]
      val size = inputStr.length

      // Initialize parser
      val parser = LibYaml.Parser()
      if (yaml_parser_initialize(parser) == 0) {
        fail("Failed to initialize parser!\n")
      }
      yaml_parser_set_input_string(parser, input, size)

      // Initialize emitter
      val buffer: Ptr[CUnsignedChar] = alloc[CUnsignedChar](BufferSize)
      val written: Ptr[CSize] = alloc[CSize]
      val emitter = LibYaml.Emitter()
      if (yaml_emitter_initialize(emitter) == 0) {
        fail("Failed to initialize emitter!\n")
      }
      yaml_emitter_set_canonical(emitter, 1)
      yaml_emitter_set_unicode(emitter, 1)
      yaml_emitter_set_encoding(emitter, Encoding.UTF8)
      yaml_emitter_set_output_string(emitter, buffer, BufferSize, written)

      // Initialize event
      val event = LibYaml.Event()
      val events = alloc[yaml_event_t](MaxEvents)

      def emitLoop(eventNum: Int): Either[String, Unit] = {
        if (yaml_parser_parse(parser, event) == 0) {
          Left("failed to parse")
        } else {
          event.typ match {
            case EventType.StreamEndEvent =>
              Right(())
            case _ if eventNum >= MaxEvents =>
              Left("too many events")
            case _ =>
              copy_event(events + eventNum, event)
              yaml_emitter_emit(emitter, event)
              // print_output
              emitLoop(eventNum + 1)
          }
        }
      }
      emitLoop(0) match {
        case Right(()) => ()
        case Left(msg) => fail(s"failed in emitLoop: $msg")
      }

      // Tear down
      yaml_emitter_delete(emitter)
      yaml_parser_delete(parser)

    }
  }

}
