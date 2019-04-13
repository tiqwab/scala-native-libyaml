import scalanative.native._

/**
  * Constants
  */
package object libyaml extends Tag {
  type TokenType = CLong
  object TokenType {
    /* An empty token. */
    val NoToken = 0

    val StreamStartToken = 1
    val StreamEndToken = 2

    val VersionDirectiveToken = 3
    val TagDirectiveToken = 4
    val DocumentStartToken = 5
    val DocumentEndToken = 6

    val BlockSequenceStartToken = 7
    val BlockMappingStartToken = 8
    val BlockEndToken = 9

    val FlowSequenceStartToken = 10
    val FlowSequenceEndToken = 11
    val FlowMappingStartToken = 12
    val FlowMappingEndToken = 13

    val BlockEntryToken = 14
    val FlowEntryToken = 15
    val KeyToken = 16
    val ValueToken = 17

    val AliasToken = 18
    val AnchorToken = 19
    val TagToken = 20
    val ScalarToken = 21
  }

  type EventType = CLong
  object EventType {
    val NoEvent = 0

    val StreamStartEvent = 1
    val StreamEndEvent = 2

    val DocumentStartEvent = 3
    val DocumentEndEvent = 4

    val AliasEvent = 5
    val ScalarEvent = 6

    val SequenceStartEvent = 7
    val SequenceEndEvent = 8

    val MappingStartEvent = 9
    val MappingEndEvent = 10
  }

  type Encoding = CLong
  object Encoding {
    val Any = 0
    val UTF8 = 1
    val UTF16LE = 2
    val UTF16BE = 3
  }

  type ScalarStyle = CLong
  object ScalarStyle {
    val Any = 0

    val Plain = 1

    val SingleQuoted = 2
    val DoubleQuoted = 3

    val Literal = 4
    val Folded = 5
  }

  type SequenceStyle = CLong with SequenceStyle.Value.type
  object SequenceStyle {
    private[libyaml] object Value

    val AnySequenceStyle = 0

    val BlockSequenceStyle = 1
    val FlowSequenceStyle = 2
  }

  type MappingStyle = CLong with MappingStyle.Value.type
  object MappingStyle {
    private[libyaml] object Value
    val AnyMappingStyle = 0

    val BlockMappingStyle = 1
    val FlowMappingStyle = 2
  }

  // node tag
  val NullTag = "tag:yaml.org,2002:null"
  val BoolTag = "tag:yaml.org,2002:bool"
  val StrTag = "tag:yaml.org,2002:str"
  val IntTag = "tag:yaml.org,2002:int"
  val FloatTag = "tag:yaml.org,2002:float"
  val TimestampTag = "tag:yaml.org,2002:timestamp"
  val SeqTag = "tag:yaml.org,2002:seq"
  val MapTag = "tag:yaml.org,2002:map"
  val DefaultScalarTag = StrTag
  val DefaultSequenceTag = SeqTag
  val DefaultMappingTag = MapTag

  type NodeType = CLong
  object NodeType {
    val NoNode = 0

    val ScalarNode = 1
    val SequenceNode = 2
    val MappingNode = 3
  }

  type Break = CLong
  object Break {
    val AnyBreak = 0
    val CRBreak = 1
    val LNBreak = 2
    val CRLNBreak = 3
  }

  type ErrorType = CLong
  object ErrorType {
    val NoError = 0
    val MemoryError = 1
    val ReaderError = 2
    val ScannerError = 3
    val ParserError = 4
    val ComposerError = 5
    val WriterError = 6
    val EmitterError = 7
  }
}
