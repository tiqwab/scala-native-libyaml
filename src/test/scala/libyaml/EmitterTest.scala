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
      |name: Alice
      |address:
      |  city: Tokyo
      |  country: Japan
      |favorites:
      |  - name: apple
      |    reason: delicious
      |  - name: banana
      |    reason: yellow
    """.stripMargin

  val BufferSize = 65536
  val MaxEvents = 1024

  /**
    * Return 1 if success, otherwise return 0.
    */
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
        yaml_alias_event_initialize(
          event_to,
          event_from.data.alias.anchor
        )
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
        yaml_sequence_start_event_initialize(
          event_to,
          event_from.data.sequence_start.anchor,
          event_from.data.sequence_start.tag,
          event_from.data.sequence_start._implicit,
          event_from.data.sequence_start.style
        )
      case EventType.SequenceEndEvent =>
        yaml_sequence_end_event_initialize(event_to)
      case EventType.MappingStartEvent =>
        yaml_mapping_start_event_initialize(
          event_to,
          event_from.data.mapping_start.anchor,
          event_from.data.mapping_start.tag,
          event_from.data.mapping_start._implicit,
          event_from.data.mapping_start.style
        )
      case EventType.MappingEndEvent =>
        yaml_mapping_end_event_initialize(event_to)
      case _ =>
        1
    }
  }

  def compare_events(event1: Ptr[clib.yaml_event_t],
                     event2: Ptr[clib.yaml_event_t])(
      implicit z: Zone): Either[String, Unit] = {
    if (event1.typ != event2.typ) {
      Left(s"type is different. event1: ${event1.typ}, event2: ${event2.typ}")
    } else {
      event1.typ match {
        case EventType.StreamStartEvent =>
          Right(())

        case EventType.DocumentStartEvent =>
          val version1 = event1.data.document_start.version_directive
          val version2 = event2.data.document_start.version_directive
          val tags1 = event1.data.document_start.tag_directives
          val tags2 = event2.data.document_start.tag_directives

          if (version1 != null && version2 == null) {
            Left("version_directive of event2 must not be null")
          } else if (version1 == null && version2 != null) {
            Left("version_directive of event2 must be null")
          } else if (version1.major != version2.major) {
            Left("major of version_directive is not same")
          } else if (version1.minor != version2.minor) {
            Left("minor of version_directive is not same")
          } else if (tags1.end - tags1.start != tags2.end - tags2.start) {
            Left("number of tag is not same")
          } else {
            val b = 0L.until(tags1.end - tags1.start).forall { k =>
              val tag1 = tags1.start + k
              val tag2 = tags2.start + k
              val handle1 = tag1.handle.cast[CString]
              val handle2 = tag2.handle.cast[CString]
              val prefix1 = tag1.prefix.cast[CString]
              val prefix2 = tag2.prefix.cast[CString]
              string.strcmp(handle1, handle2) == 0 &&
              string.strcmp(prefix1, prefix2) == 0
            }
            if (b) {
              Right(())
            } else {
              Left("some tags does not match")
            }
          }

        case EventType.DocumentEndEvent =>
          Right(())

        case EventType.AliasEvent =>
          Left("not yet implemented for AliasEvent")

        case EventType.ScalarEvent =>
          val anchor1 = event1.data.scalar.anchor.cast[CString]
          val anchor2 = event2.data.scalar.anchor.cast[CString]
          val tag1 = event1.data.scalar.tag.cast[CString]
          val tag2 = event2.data.scalar.tag.cast[CString]
          val length1 = event1.data.scalar.length
          val length2 = event2.data.scalar.length
          val value1 = event1.data.scalar.value.cast[CString]
          val value2 = event2.data.scalar.value.cast[CString]
          val plain_implicit1 = event1.data.scalar.plain_implicit
          val plain_implicit2 = event2.data.scalar.plain_implicit
          val quoted_implicit1 = event1.data.scalar.quoted_implicit
          val quoted_implicit2 = event2.data.scalar.quoted_implicit

          if (anchor1 != null && anchor2 == null) {
            Left("anchor of scalar event2 must not be null")
          } else if (anchor1 == null && anchor2 != null) {
            Left("anchor of scalar event2 must be null")
          } else if (anchor1 != null && anchor2 != null &&
                     string.strcmp(anchor1, anchor2) != 0) {
            Left("anchor of scalar event is not same")
          } else if (tag1 != null && tag2 == null &&
                     string.strcmp(tag1, toCString("!")) != 0) {
            Left("tag of scalar event2 must not be null")
          } else if (tag1 == null && tag2 != null &&
                     string.strcmp(tag2, toCString("!")) != 0) {
            Left("tag of scalar event2 must be null")
          } else if (tag1 != null && tag2 != null &&
                     string.strcmp(tag1, tag2) != 0) {
            Left("tas of scalar event is not same")
          } else if (length1 != length2) {
            Left("length of scalar is not same")
          } else if (string.memcmp(value1, value2, length1) != 0) {
            Left("value of scalar event is not same")
          } else if (plain_implicit1 != plain_implicit2) {
            Left("plain_implicit of scalar event is not same")
          } else if (quoted_implicit1 != quoted_implicit2) {
            Left("quoted_implicit of scalar event is not same")
          } else {
            stdio.printf(toCString("%s, %s\n"), value1, value2)
            Right(())
          }

        case EventType.SequenceStartEvent =>
          val anchor1 = event1.data.sequence_start.anchor.cast[CString]
          val anchor2 = event2.data.sequence_start.anchor.cast[CString]
          val tag1 = event1.data.sequence_start.tag.cast[CString]
          val tag2 = event2.data.sequence_start.tag.cast[CString]
          val implicit1 = event1.data.sequence_start._implicit
          val implicit2 = event2.data.sequence_start._implicit

          if (anchor1 != null && anchor2 == null) {
            Left("anchor of sequence event2 must not be null")
          } else if (anchor1 == null && anchor2 != null) {
            Left("anchor of sequence event2 must be null")
          } else if (anchor1 != null && anchor2 != null &&
                     string.strcmp(anchor1, anchor2) != 0) {
            Left("anchor of sequence event is not same")
          } else if (tag1 != null && tag2 == null) {
            Left("tag of sequence event2 must not be null")
          } else if (tag1 == null && tag2 != null) {
            Left("tag of sequence event2 must be null")
          } else if (tag1 != null && tag2 != null &&
                     string.strcmp(tag1, tag2) != 0) {
            Left("tag of sequence event is not same")
          } else if (implicit1 != implicit2) {
            Left("implicit of sequence event is not same")
          } else {
            Right(())
          }

        case EventType.MappingStartEvent =>
          val anchor1 = event1.data.mapping_start.anchor.cast[CString]
          val anchor2 = event2.data.mapping_start.anchor.cast[CString]
          val tag1 = event1.data.mapping_start.tag.cast[CString]
          val tag2 = event2.data.mapping_start.tag.cast[CString]
          val implicit1 = event1.data.mapping_start._implicit
          val implicit2 = event2.data.mapping_start._implicit

          if (anchor1 != null && anchor2 == null) {
            Left("anchor of mapping event2 must not be null")
          } else if (anchor1 == null && anchor2 != null) {
            Left("anchor of mapping event2 must be null")
          } else if (anchor1 != null && anchor2 != null &&
                     string.strcmp(anchor1, anchor2) != 0) {
            Left("anchor of mapping event is not same")
          } else if (tag1 != null && tag2 == null) {
            Left("tag of mapping event2 must not be null")
          } else if (tag1 == null && tag2 != null) {
            Left("tag of mapping event2 must be null")
          } else if (tag1 != null && tag2 != null &&
                     string.strcmp(tag1, tag2) != 0) {
            Left("tag of mapping event is not same")
          } else if (implicit1 != implicit2) {
            Left("implicit of mapping event is not same")
          } else {
            Right(())
          }

        case _ =>
          Right(())
      }
    }
  }

  test("should emit events") {
    Zone { implicit z =>
      val input = toCString(inputStr).cast[Ptr[CUnsignedChar]]
      val size = inputStr.length

      // Initialize parser
      val parser = LibYaml.Parser()
      if (yaml_parser_initialize(parser) == 0) {
        fail("Failed to initialize parser")
      }
      yaml_parser_set_input_string(parser, input, size)

      // Initialize emitter
      val buffer: Ptr[CUnsignedChar] = alloc[CUnsignedChar](BufferSize)
      val written: Ptr[CSize] = alloc[CSize]
      val emitter = LibYaml.Emitter()
      if (yaml_emitter_initialize(emitter) == 0) {
        fail("Failed to initialize emitter")
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
          Left("Failed to parse")
        } else {
          event.typ match {
            case EventType.StreamEndEvent =>
              Right(())
            case _ if eventNum >= MaxEvents =>
              Left("Too many events")
            case _ =>
              copy_event(events + eventNum, event)
              yaml_emitter_emit(emitter, event)
              // print_output
              emitLoop(eventNum + 1)
          }
        }
      }
      emitLoop(0) match {
        case Right(_)  => ()
        case Left(msg) => fail(s"Failed in emitLoop: $msg")
      }

      yaml_emitter_delete(emitter)
      yaml_parser_delete(parser)

      if (yaml_parser_initialize(parser) == 0) {
        fail("Failed to initialize parser")
      }
      yaml_parser_set_input_string(parser, input, size)

      def compareEventsLoop(eventNum: Int): Either[String, Unit] = {
        if (yaml_parser_parse(parser, event) == 0) {
          Left("Failed to parse")
        } else {
          event.typ match {
            case EventType.StreamEndEvent =>
              Right(())
            case _ =>
              // stdio.printf(toCString("%d\n"), (events + eventNum).typ)
              val res = compare_events(events + eventNum, event)
              yaml_event_delete(event)
              res match {
                case Right(_)    => compareEventsLoop(eventNum + 1)
                case l @ Left(_) => l
              }
          }
        }
      }
      compareEventsLoop(0) match {
        case Right(_)  => ()
        case Left(msg) => fail(s"Failed in compareEventsLoop: $msg")
      }

      yaml_parser_delete(parser)
      0 until MaxEvents foreach { i =>
        yaml_event_delete(events + i)
      }
    }
  }

}
