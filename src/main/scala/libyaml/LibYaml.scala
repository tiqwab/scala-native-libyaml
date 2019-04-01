package libyaml

import scalanative.native._

object LibYaml {
  object Parser {
    def apply()(implicit z: Zone): Ptr[clib.yaml_parser_t] = {
      z.alloc(480).cast[Ptr[clib.yaml_parser_t]] // FIXME
      // alloc[clib.yaml_parser_t]
    }
  }

  object Token {
    def apply()(implicit z: Zone): Ptr[clib.yaml_token_t] = {
      alloc[clib.yaml_token_t]
    }
  }
}
