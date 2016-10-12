package org.example

import org.spoofax.interpreter.terms.IStrategoTerm

case class IDs(defsRefs: List[DefRef], o: Origin)

sealed abstract class DefRef
case class Def(id: String, o: Option[Origin]) extends DefRef
case class Ref(id: String, o: Option[Origin]) extends DefRef

case class IDsAnalysisResult(
  ast: IDs,
  errors: List[EditorMessage],
  warnings: List[EditorMessage],
  notes: List[EditorMessage])

object EntityScala {

  def termToIDs(term: STerm): IDs = term match {
    case SCons("IDs", SList(ids, _) :: Nil, Some(o)) => IDs(ids.map(termToDefRef), o)
    case _ => throw new Exception("Failed parsing")
  }

  def termToDefRef(term: STerm): DefRef = term match {
    case SCons("Def", SString(id, o) :: Nil, _) => Def(id, o)
    case SCons("Ref", SString(id, o) :: Nil, _) => Ref(id, o)
    case _ => throw new Exception("Failed parsing")
  }

  def IDsToTerm(ids: IDs): STerm =
    SCons("IDs", SList(ids.defsRefs.map(defRefToTerm), None) :: Nil, Some(ids.o))

  def defRefToTerm(dr: DefRef): STerm = dr match {
    case Def(id, o) => SCons("Def", SString(id, o) :: Nil, None)
    case Ref(id, o) => SCons("Ref", SString(id, o) :: Nil, None)
  }

  def toAnalysisResult(ar: IDsAnalysisResult): AnalysisResult =
    AnalysisResult(IDsToTerm(ar.ast), ar.errors, ar.warnings, ar.notes)

}