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

  object Event {
    def apply()(implicit z: Zone): Ptr[clib.yaml_event_t] = {
      alloc[clib.yaml_event_t]
    }
  }

  object Document {
    def apply()(implicit z: Zone): Ptr[clib.yaml_document_t] = {
      alloc[clib.yaml_document_t]
    }
  }

  object Emitter {
    def apply()(implicit z: Zone): Ptr[clib.yaml_emitter_t] = {
      z.alloc(432).cast[Ptr[clib.yaml_emitter_t]] // FIXME
      // alloc[clib.yaml_emitter_t]
    }
  }
}
