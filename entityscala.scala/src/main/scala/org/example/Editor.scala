package org.example

import org.spoofax.interpreter.terms.IStrategoTerm

case class TupleOfThree(
  ast: Term,
  path: java.lang.String,
  projectPath: java.lang.String)

case class AnalysisResult(
  ast: Term,
  errors: scala.collection.immutable.List[EditorMessage],
  warnings: scala.collection.immutable.List[EditorMessage],
  notes: scala.collection.immutable.List[EditorMessage])

case class EditorMessage(
  origin: Origin,
  msg: java.lang.String)

object Editor {

  def toTupleOfThree(term: Term): Option[TupleOfThree] = term match {
    case Tuple(ast :: String(path, _) :: String(projectPath, _) :: Nil, _) => Some(TupleOfThree(ast, path, projectPath))
    case _ => None
  }

  def fromAnalysisResult(ar: AnalysisResult): Term =
    Tuple(scala.collection.immutable.List(
      ar.ast,
      List(ar.errors.map(fromEditorMessage), None),
      List(ar.warnings.map(fromEditorMessage), None),
      List(ar.notes.map(fromEditorMessage), None)), None)

  def fromEditorMessage(em: EditorMessage): Term =
    Tuple(scala.collection.immutable.List(String("origin dummy", Some(em.origin)), String(em.msg, None)), None)

}