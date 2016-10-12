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

  def foo(x: Array[String]) = x.foldLeft("")((a, b) => a + b)

  def main(args: Array[String]) {
    println("Hello World!")
    println("concat arguments = " + foo(args))
  }

  def runAsStrategy(context: Context, inputFromEditor: IStrategoTerm): IStrategoTerm = {
    // output to the Spoofax console is handled via Context.getIOAgent.printError
    context.getIOAgent.printError("Hello, World! Your input was: ")
    context.getIOAgent.printError(inputFromEditor.toString)
    // you return a tuple for the file you want to show in the editor
    // tuple: (filename as IStrategoString, contents as IStrategoString)
    // trivial strategy: don't output any files == None()
    context.getFactory.makeAppl(context.getFactory.makeConstructor("None", 0))

    val scalaVal = STerm.fromStratego(inputFromEditor);
    context.getIOAgent.printError(scalaVal.toString());

    val ast = inputFromEditor.getAllSubterms()(0);
    val scalaAst = STerm.fromStratego(ast);
    context.getIOAgent.printError(scalaAst.toString());

    val origin = ImploderAttachment.get(tryGetOrigin(ast));
    origin.getLeftToken.getIndex

    context.getIOAgent.printError(origin.toString());
    context.getIOAgent.printError(origin.getLeftToken.getStartOffset.toString);
    context.getIOAgent.printError(origin.getLeftToken.getEndOffset.toString);
    context.getIOAgent.printError(origin.getRightToken.getStartOffset.toString);
    context.getIOAgent.printError(origin.getRightToken.getEndOffset.toString);

    val origin2 = ImploderAttachment.get(tryGetOrigin(inputFromEditor));
    context.getIOAgent.printError(if (origin2 == null) "null" else origin2.toString());

    val astOrig = tryGetOrigin(ast);
    context.getIOAgent.printError(astOrig.toString());

    val strategoVal = STerm.toStratego(scalaVal)(context)

    return strategoVal
    //    return context.getFactory.makeString("Regards from scala-strategy")
  }

  def editorAnalyze(context: Context, strTerm: IStrategoTerm): IStrategoTerm =
    editorAnalyze(strTerm)(context)

  def editorAnalyze(strTerm: IStrategoTerm)(implicit context: Context): IStrategoTerm = {
    //    debug("editorAnalyze")
    //    debug(strTerm.toString())
    val term = STerm.fromStratego(strTerm)
    //    debug(term.toString())
    val tt3 = Editor.toTupleOfThree(term).getOrElse(throw new Exception("No Tuple of Three provided."))
    //    debug(tt3.toString())
    val entityscala = EntityScala.termToIDs(tt3.ast)
    //    debug(entityscala.toString())
    val entityscalaAnalyzed = editorAnalyze(entityscala)
    val ar = EntityScala.toAnalysisResult(entityscalaAnalyzed)
    //    debug(ar.toString())
    val ar2 = Editor.fromAnalysisResult(ar)
    //    debug(ar2.toString())
    val strAr = STerm.toStratego(ar2)
    //    debug(strAr.toString())
    strAr
  }

  def editorAnalyze(ids: IDs)(implicit context: Context): IDsAnalysisResult = {
    val model = Model.buildModel(ids)
    val errors = Model.getErrors(model)
    val idsDesugared = IDs(Def("addedDuringAnalysis", None) :: ids.defsRefs, ids.o)
    val ar = IDsAnalysisResult(idsDesugared, errors, Nil, Nil)
    ar
  }

  def debug(msg: String)(implicit context: Context) = context.getIOAgent.printError(msg);

}
