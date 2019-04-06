package libyaml

import libyaml.clib._
import libyaml.clib.implicits._
import minitest._

import scala.scalanative.native._

/**
  * ref. https://github.com/yaml/libyaml/blob/master/tests/run-dumper.c
  */
object CopyDocumentTest extends SimpleTestSuite {

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

  def copy_document(document_to: Ptr[yaml_document_t],
                    document_from: Ptr[yaml_document_t])(
      implicit z: Zone): Either[String, Unit] = {
    val initializedEither = {
      val res = yaml_document_initialize(
        document_to,
        document_from.version_directive,
        document_from.tag_directives.start,
        document_from.tag_directives.end,
        document_from.start_implicit,
        document_from.end_implicit
      )
      Either.cond(res != 0, (), "failed to initialize doc")
    }

    def addNodeLoop(i: Int): Either[String, Unit] = {
      val node = document_from.nodes.start + i
      // FIXME: `(node - document_from.nodes.top)` does not work as expected
      if (node.cast[CPtrDiff] - document_from.nodes.top.cast[CPtrDiff] >= 0) {
        Right(())
      } else {
        stdio.printf(toCString("node: %d, %d\n"), i, node.typ)
        node.typ match {
          case NodeType.ScalarNode =>
            if (yaml_document_add_scalar(document_to,
                                         node.tag,
                                         node.data.scalar.value,
                                         node.data.scalar.length.toInt,
                                         node.data.scalar.style) == 0) {
              Left("failed to add scalar node")
            } else {
              addNodeLoop(i + 1)
            }
          case NodeType.SequenceNode =>
            if (yaml_document_add_sequence(document_to,
                                           node.tag,
                                           node.data.sequence.style) == 0) {
              Left("failed to add sequence node")
            } else {
              addNodeLoop(i + 1)
            }
          case NodeType.MappingNode =>
            if (yaml_document_add_mapping(document_to,
                                          node.tag,
                                          node.data.mapping.style) == 0) {
              Left("failed to add mapping node")
            } else {
              addNodeLoop(i + 1)
            }
          case NodeType.NoNode =>
            Left("NodeType.NoNode is unexpected")
        }
      }
    }

    def makeRelationLoop(i: Int): Either[String, Unit] = {
      val node = document_from.nodes.start + i
      // FIXME: `(node - document_from.nodes.top)` does not work as expected
      if (node.cast[CPtrDiff] - document_from.nodes.top.cast[CPtrDiff] >= 0) {
        Right(())
      } else {
        node.typ match {
          case NodeType.SequenceNode =>
            def sequenceLoop(j: Int): Either[String, Unit] = {
              val item = node.data.sequence.items.start + j
              if (item - node.data.sequence.items.top >= 0) {
                Right(())
              } else {
                stdio.printf(toCString("sequence: %d, %d, %d\n"), i, j, !item)
                if (yaml_document_append_sequence_item(document_to,
                                                       i + 1,
                                                       !item) == 0) {
                  Left("failed to make relation of sequence node")
                } else {
                  sequenceLoop(j + 1)
                }
              }
            }

            for {
              _ <- sequenceLoop(0).right
              _ <- makeRelationLoop(i + 1).right
            } yield ()

          case NodeType.MappingNode =>
            def mappingLoop(j: Int): Either[String, Unit] = {
              val pair = node.data.mapping.pairs.start + j
              if (pair - node.data.mapping.pairs.top >= 0) {
                Right(())
              } else {
                stdio.printf(toCString("mapping: %d, %d, %d, %d\n"),
                             i,
                             j,
                             pair.key,
                             pair.value)
                if (yaml_document_append_mapping_pair(document_to,
                                                      i + 1,
                                                      pair.key,
                                                      pair.value) == 0) {
                  Left("failed to make relation of mapping")
                } else {
                  mappingLoop(j + 1)
                }
              }
            }

            for {
              _ <- mappingLoop(0).right
              _ <- makeRelationLoop(i + 1).right
            } yield ()

          case _ =>
            makeRelationLoop(i + 1)
        }
      }
    }

    for {
      _ <- initializedEither.right
      _ <- addNodeLoop(0).right
      _ <- makeRelationLoop(0).right
    } yield ()
  }

  /**
    * Return 1 if the two nodes are same, otherwise return 0.
    * @return
    */
  def compare_nodes(document1: Ptr[yaml_document_t],
                    index1: CInt,
                    document2: Ptr[yaml_document_t],
                    index2: CInt,
                    level: CInt)(implicit z: Zone): CInt = {
    if (level > 1000) {
      return 0
    }

    val node1 = yaml_document_get_node(document1, index1)
    val node2 = yaml_document_get_node(document2, index2)
    if (node1 == null) {
      stdio.printf(toCString("node1 should not be null\n"))
      return 0
    }
    if (node2 == null) {
      stdio.printf(toCString("node2 should not be null\n"))
      return 0
    }

    if (node1.typ != node2.typ) {
      return 0
    }

    if (string.strcmp(node1.tag.cast[CString], node2.tag.cast[CString]) != 0) {
      return 0
    }

    node1.typ match {
      case NodeType.ScalarNode =>
        if (node1.data.scalar.length != node2.data.scalar.length) {
          return 0
        }
        if (string.strcmp(node1.data.scalar.value.cast[CString],
                          node2.data.scalar.value.cast[CString]) != 0) {
          return 0
        }
      case NodeType.SequenceNode =>
        if ((node1.data.sequence.items.top - node1.data.sequence.items.start) != (node2.data.sequence.items.top - node2.data.sequence.items.start)) {
          return 0
        }

        def sequenceLoop(k: Int): Int = {
          val pair1 = node1.data.sequence.items.start + k
          val pair2 = node2.data.sequence.items.start + k
          if (pair1 - node1.data.sequence.items.top >= 0) {
            1
          } else {
            if (compare_nodes(document1, !pair1, document2, !pair2, level) == 0) {
              0
            } else {
              sequenceLoop(k + 1)
            }
          }
        }

        if (sequenceLoop(0) == 0) {
          return 0
        }
      case NodeType.MappingNode =>
        if ((node1.data.mapping.pairs.top - node1.data.mapping.pairs.start) != (node2.data.mapping.pairs.top - node2.data.mapping.pairs.start)) {
          return 0
        }
        // FIXME: 'until' not 'to'?
        0L.until(node1.data.mapping.pairs.top - node1.data.mapping.pairs.start)
          .foreach { k =>
            val pair1 = node1.data.mapping.pairs.start + k
            val pair2 = node2.data.mapping.pairs.start + k
            if (compare_nodes(document1, pair1.key, document2, pair2.key, level) == 0) {
              return 0
            }
            if (compare_nodes(document1,
                              pair1.value,
                              document2,
                              pair2.value,
                              level) == 0) {
              return 0
            }
          }
      case NodeType.NoNode =>
        stdio.printf(toCString("NodeType.NoNode is unexpected\n"))
        return 0
    }

    return 1
  }

  /**
    * Return 1 if the two documents are same, otherwise return 0.
    */
  def compare_documents(
      document1: Ptr[yaml_document_t],
      document2: Ptr[yaml_document_t])(implicit z: Zone): CInt = {
    // compare version directive
    if ((document1.version_directive != null && document2.version_directive == null)
        || (document1.version_directive == null && document2.version_directive != null)
        || (
          (document1.version_directive != null && document2.version_directive != null)
          && (
            (document1.version_directive.major != document2.version_directive.major)
            || (document1.version_directive.minor != document2.version_directive.minor)
          )
        )) {
      return 0
    }

    // compare number of tag directives
    if ((document1.tag_directives.end - document1.tag_directives.start) !=
          (document2.tag_directives.end - document2.tag_directives.start)) {
      return 0
    }

    // compare each tag directive
    val existsDifferentTagDirective =
      0L.until(document1.tag_directives.end - document1.tag_directives.start)
        .exists { k =>
          val cmpHandle = string.strcmp(
            (document1.tag_directives.start + k).handle.cast[CString],
            (document2.tag_directives.start + k).handle.cast[CString])
          val cmpPrefix = string.strcmp(
            (document1.tag_directives.start + k).prefix.cast[CString],
            (document2.tag_directives.start + k).prefix.cast[CString])
          (cmpHandle != 0) || (cmpPrefix != 0)
        }
    if (existsDifferentTagDirective) {
      return 0
    }

    // compare number of nodes
    if ((document1.nodes.top - document1.nodes.start) != (document2.nodes.top - document2.nodes.start)) {
      return 0
    }

    // compare each node
    if (document1.nodes.top != document1.nodes.start) {
      if (compare_nodes(document1, 1, document2, 1, 0) == 0) {
        return 0
      }
    }

    return 1
  }

  test("should copy document") {
    Zone { implicit z =>
      val input = toCString(inputStr).cast[Ptr[CUnsignedChar]]
      val size = inputStr.length

      // Initialize parser
      val parser = LibYaml.Parser()
      if (yaml_parser_initialize(parser) == 0) {
        fail("Failed to initialize parser!\n")
      }

      // Set input
      yaml_parser_set_input_string(parser, input, size)

      // Parse to document
      val srcDoc = LibYaml.Document()
      if (yaml_parser_load(parser, srcDoc) == 0) {
        fail("Failed to parse document!\n")
      }

      val root = yaml_document_get_root_node(srcDoc)
      assert(root != null, "root should not be null")

      val destDoc = LibYaml.Document()

      copy_document(destDoc, srcDoc) match {
        case Right(_)  => ()
        case Left(msg) => fail(s"failed to copy document: $msg")
      }
      assert(compare_documents(srcDoc, destDoc) == 1, "not same document")

      yaml_document_delete(srcDoc)
      yaml_document_delete(destDoc)
      yaml_parser_delete(parser)
    }
  }
}
