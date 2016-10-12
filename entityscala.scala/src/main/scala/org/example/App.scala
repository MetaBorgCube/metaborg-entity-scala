package org.example

import org.spoofax.interpreter.terms.IStrategoTerm
import org.spoofax.interpreter.terms.IStrategoAppl
import org.strategoxt.lang.Context
import org.spoofax.jsglr.client.imploder.ImploderAttachment
import org.spoofax.terms.attachments.OriginAttachment.tryGetOrigin;

//import org.example.Term

/**
 * @author ${user.name}
 */
object App {

  def main(args: Array[String]) {
    println("Hello World!")
  }

  def editorAnalyze(context: Context, strTerm: IStrategoTerm): IStrategoTerm =
    editorAnalyze(strTerm)(context)

  def editorAnalyze(strTerm: IStrategoTerm)(implicit context: Context): IStrategoTerm = {
    val term = STerm.fromStratego(strTerm)
    val tt3 = Editor.toTupleOfThree(term).getOrElse(throw new Exception("No Tuple of Three provided."))
    val entityscala = EntityScala.termToIDs(tt3.ast)
    val entityscalaAnalyzed = editorAnalyze(entityscala)
    val ar = EntityScala.toAnalysisResult(entityscalaAnalyzed)
    val ar2 = Editor.fromAnalysisResult(ar)
    val strAr = STerm.toStratego(ar2)
    strAr
  }

  def editorAnalyze(ids: IDs)(implicit context: Context): IDsAnalysisResult = {
    val model = Model.buildModel(ids)
    val errors = Model.getErrors(model)
    val idsDesugared = IDs(Def("addedDuringAnalysis", None) :: ids.defsRefs, ids.o)
    val ar = IDsAnalysisResult(idsDesugared, errors, Nil, Nil)
    ar
  }

  def editorResolve(context: Context, strTerm: IStrategoTerm): IStrategoTerm =
    editorResolve(strTerm)(context)

  def editorResolve(strTerm: IStrategoTerm)(implicit context: Context): IStrategoTerm = {
    val term = STerm.fromStratego(strTerm)
    val tt5 = Editor.toTupleOfFive(term).getOrElse(throw new Exception("No Tuple of Five provided."))
    val entityscala = EntityScala.termToIDs(tt5.ast)
    val ref = tt5.node.o
    val resolveTo = editorResolve(entityscala, ref)
    STerm.toStratego(SString("dummy string", resolveTo))
  }

  def editorResolve(ids: IDs, ref: Option[Origin])(implicit context: Context): Option[Origin] = ref match {
    case None => None
    case Some(r) =>
      val model = Model.buildModel(ids)
      Model.resolveReference(model, r)
  }

  def editorHover(context: Context, strTerm: IStrategoTerm): IStrategoTerm =
    editorHover(strTerm)(context)

  def editorHover(strTerm: IStrategoTerm)(implicit context: Context): IStrategoTerm = {
    val term = STerm.fromStratego(strTerm)
    val tt5 = Editor.toTupleOfFive(term).getOrElse(throw new Exception("No Tuple of Five provided."))
    val entityscala = EntityScala.termToIDs(tt5.ast)
    STerm.toStratego(SString("<b>Hovering</b><br/>" + STerm.toStratego(tt5.node).toString() + "<br/>" + tt5.node.toString(), None))
  }

  def debug(msg: String)(implicit context: Context) = context.getIOAgent.printError(msg);

}
