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

    val scalaVal = Term.fromStratego(inputFromEditor);
    context.getIOAgent.printError(scalaVal.toString());

    val ast = inputFromEditor.getAllSubterms()(0);
    val scalaAst = Term.fromStratego(ast);
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

    val strategoVal = Term.toStratego(scalaVal)(context.getFactory)

    return strategoVal
    //    return context.getFactory.makeString("Regards from scala-strategy")
  }

  def editorAnalyze(context: Context, strTerm: IStrategoTerm): IStrategoTerm = {
    debug("editorAnalyze")(context)
    debug(strTerm.toString())(context)
    val term = Term.fromStratego(strTerm)
    debug(term.toString())(context)
    val tt3 = Editor.toTupleOfThree(term).getOrElse(throw new Exception("No Tuple of Three provided."))
    debug(tt3.toString())(context)
    val entityscala = EntityScala.termToIDs(tt3.ast)
    debug(entityscala.toString())(context)
    val warnings = entityscala.defsRefs.collect { case a: Def => a }.map { x => EditorMessage(x.o, "some error message on definition "+x.id) }
    val ar = AnalysisResult(tt3.ast, Nil, warnings, Nil)
    debug(ar.toString())(context)
    val ar2 = Editor.fromAnalysisResult(ar)
    debug(ar2.toString())(context)
    val strAr = Term.toStratego(ar2)(context.getFactory)
    debug(strAr.toString())(context)
    strAr
  }

  def debug(msg: java.lang.String)(implicit context: Context) = context.getIOAgent.printError(msg);

}
