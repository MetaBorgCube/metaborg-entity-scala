package org.example

import org.spoofax.interpreter.terms.IStrategoTerm

case class IDs(defsRefs: scala.collection.immutable.List[DefRef], o: Origin)

sealed abstract class DefRef
case class Def(id: java.lang.String, o: Option[Origin]) extends DefRef
case class Ref(id: java.lang.String, o: Option[Origin]) extends DefRef

object EntityScala {

  def termToIDs(term: Term): IDs = term match {
    case Cons("IDs", List(ids, _) :: Nil, Some(o)) => IDs(ids.map(termToDefRef), o)
    case _ => throw new Exception("Failed parsing")
  }

  def termToDefRef(term: Term): DefRef = term match {
    case Cons("Def", String(id, o) :: Nil, _) => Def(id, o)
    case Cons("Ref", String(id, o) :: Nil, _) => Ref(id, o)
    case _ => throw new Exception("Failed parsing")
  }
  
  def IDsToTerm(ids: IDs): Term = 
    Cons("IDs", List(ids.defsRefs.map(defRefToTerm), None) :: Nil, Some(ids.o))

  def defRefToTerm(dr: DefRef): Term = dr match {
    case Def(id, o) => Cons("Def", String(id, o) :: Nil, None)
    case Ref(id, o) => Cons("Ref", String(id, o) :: Nil, None)
  }

}