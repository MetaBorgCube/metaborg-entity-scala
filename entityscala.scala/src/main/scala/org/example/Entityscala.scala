package org.example

import org.spoofax.interpreter.terms.IStrategoTerm

case class IDs(defsRefs: scala.collection.immutable.List[DefRef], o: Origin)

sealed abstract class DefRef
case class Def(id: java.lang.String, o: Origin) extends DefRef
case class Ref(id: java.lang.String, o: Origin) extends DefRef

object EntityScala {

  def termToIDs(term: Term): IDs = term match {
    case Cons("IDs", List(ids, _) :: Nil, Some(o)) => IDs(ids.map(termToDefRef), o)
    case _ => throw new Exception("Failed parsing")
  }

  def termToDefRef(term: Term): DefRef = term match {
    case Cons("Def", String(id, Some(o)) :: Nil, _) => Def(id, o)
    case Cons("Ref", String(id, Some(o)) :: Nil, _) => Ref(id, o)
    case _ => throw new Exception("Failed parsing")
  }

}