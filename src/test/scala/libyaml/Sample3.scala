package libyaml

import libyaml.clib._
import minitest._

import scalanative.native._
import scalanative.native.stdio._

/**
  * Run the third sample code in https://www.wpsoftware.net/andrew/pages/libyaml.html
  */
object Sample3 extends SimpleTestSuite {

  test("should run the third sample code") {
    Zone { implicit zone =>
      val fh = fopen(toCString("sample/config/public.yaml"), toCString("r"))
      val parser = LibYaml.Parser()
      val event = LibYaml.Event()

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
        yaml_parser_parse(parser, event)
        !event._1 match {
          case EventType.NoEvent =>
            puts(toCString("No event!"))
          case EventType.StreamStartEvent =>
            puts(toCString("STREAM START"))
          case EventType.StreamEndEvent =>
            puts(toCString("STREAM_END"))
          case EventType.DocumentStartEvent =>
            puts(toCString("<b>Start Document</b>"))
          case EventType.DocumentEndEvent =>
            puts(toCString("<b>End Document</b>"))
          case EventType.SequenceStartEvent =>
            puts(toCString("<b>Start Sequence</b>"))
          case EventType.SequenceEndEvent =>
            puts(toCString("<b>End Sequence</b>"))
          case EventType.MappingStartEvent =>
            puts(toCString("<b>Start Mapping</b>"))
          case EventType.MappingEndEvent =>
            puts(toCString("<b>End Mapping</b>"))
          case EventType.AliasEvent =>
            printf(toCString("Got alias (anchor %s)\n"),
                   !event._2.cast[Ptr[yaml_event_alias]]._1)
          case EventType.ScalarEvent =>
            printf(toCString("Got scalar (value %s)\n"),
                   !event._2.cast[Ptr[yaml_event_scalar]]._3)
          case _ =>
            fail(s"unexpected type: ${!event._1}\n")
        }
        if (!event._1 != EventType.StreamEndEvent) {
          yaml_event_delete(event)
        }
      } while (!event._1 != EventType.StreamEndEvent)
      yaml_event_delete(event)

      // Cleanup
      yaml_parser_delete(parser)
      fclose(fh)

      ()
    }
  }

}
