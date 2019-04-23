# scala-native-libyaml

scala-native bindings for [libyaml][1].

## Dependency

For `build.sbt`:

```scala
libraryDependencies ++= Seq(
  "com.tiqwab" %%% "scala-native-libyaml" % "0.0.2"
)
```

## Example

A simple example to parse yaml document.

```scala
package example

import libyaml.LibYaml
import libyaml.clib._
import libyaml.clib.implicits._

import scala.scalanative.native._

object Main {

  def main(args: Array[String]): Unit = {
    Zone { implicit z =>
      // prepare input yaml
      val inputStr =
        """
          |%YAML 1.1
          |%TAG !yaml! tag:yaml.org,2002:
          |---
          |name: Alice
          |favorites:
          |  - apple
          |  - banana
        """.stripMargin

      val input = toCString(inputStr).cast[Ptr[CUnsignedChar]]
      val size = inputStr.length

      // initialize parser
      val parser = LibYaml.Parser()
      if (yaml_parser_initialize(parser) == 0) {
        stdio.fputs(toCString("failed to initialize parser"), stdio.stderr)
        stdlib.exit(1)
      }

      // set input
      yaml_parser_set_input_string(parser, input, size)

      // parse document
      val document = LibYaml.Document()
      if (yaml_parser_load(parser, document) == 0) {
        stdio.fputs(toCString("failed to parse document\n"), stdio.stderr)
        stdlib.exit(1)
      }

      stdio.printf(toCString("yaml version of document is: %d.%d\n"),
                   document.version_directive.major,
                   document.version_directive.minor)

      // cleanup
      yaml_document_delete(document)
      yaml_parser_delete(parser)
    }
  }

}
```

See [tests][2] for other examples.

## License

MIT License.

The function interfaces of scala-native-libyaml are derived from `yaml.h` in libyaml, which is licensed under MIT License.

[1]: https://github.com/yaml/libyaml
[2]: https://github.com/tiqwab/scala-native-libyaml/tree/master/src/test/scala/libyaml
