package libyaml

trait Tag {
  type Tagged[T] = { type Tag = T }
  type @@[A, T] = A with Tagged[T]

  def tagged[A, T](a: A): A @@ T = a.asInstanceOf[A @@ T]
}
