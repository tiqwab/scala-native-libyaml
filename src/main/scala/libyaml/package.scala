import scalanative.native._

/**
  * Constants
  */
package object libyaml {
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
}
