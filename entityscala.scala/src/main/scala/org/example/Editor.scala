package org.example

import org.spoofax.interpreter.terms.IStrategoTerm

case class TupleOfThree(
  ast: STerm,
  path: String,
  projectPath: String)

case class AnalysisResult(
  ast: STerm,
  errors: List[EditorMessage],
  warnings: List[EditorMessage],
  notes: List[EditorMessage])

case class EditorMessage(
  origin: Origin,
  msg: String)

object Editor {

  def toTupleOfThree(term: STerm): Option[TupleOfThree] = term match {
    case STuple(ast :: SString(path, _) :: SString(projectPath, _) :: Nil, _) => Some(TupleOfThree(ast, path, projectPath))
    case _ => None
  }

  def fromAnalysisResult(ar: AnalysisResult): STerm =
    STuple(List(
      ar.ast,
      SList(ar.errors.map(fromEditorMessage), None),
      SList(ar.warnings.map(fromEditorMessage), None),
      SList(ar.notes.map(fromEditorMessage), None)), None)

  def fromEditorMessage(em: EditorMessage): STerm =
    STuple(List(SString("origin dummy", Some(em.origin)), SString(em.msg, None)), None)

}