package org.example

import org.spoofax.interpreter.terms.IStrategoTerm
import org.spoofax.interpreter.terms.IStrategoInt
import org.spoofax.interpreter.terms.IStrategoReal
import org.spoofax.interpreter.terms.IStrategoString
import org.spoofax.interpreter.terms.IStrategoList
import collection.JavaConverters._
import org.spoofax.interpreter.terms.IStrategoTuple
import org.spoofax.interpreter.terms.IStrategoAppl
import org.spoofax.interpreter.terms.ITermFactory
import org.spoofax.jsglr.client.imploder.ImploderAttachment
import org.spoofax.terms.attachments.OriginAttachment.tryGetOrigin
import org.strategoxt.lang.Context

sealed abstract class STerm {
  val o: Option[Origin]
}

case class SInt(i: scala.Int, o: Option[Origin]) extends STerm
case class SReal(d: Double, o: Option[Origin]) extends STerm
case class SString(s: String, o: Option[Origin]) extends STerm
case class SList(l: List[STerm], o: Option[Origin]) extends STerm
case class STuple(l: List[STerm], o: Option[Origin]) extends STerm
case class SCons(c: String, l: List[STerm], o: Option[Origin]) extends STerm

case class Origin(
  filename: String,
  line: scala.Int,
  column: scala.Int,
  startOffset: scala.Int,
  endOffset: scala.Int)

object STerm {
  def fromStratego(term: IStrategoTerm): STerm = {
    term.getTermType match {
      case IStrategoTerm.INT => SInt(term.asInstanceOf[IStrategoInt].intValue, getOrigin(term))
      case IStrategoTerm.REAL => SReal(term.asInstanceOf[IStrategoReal].realValue, getOrigin(term))
      case IStrategoTerm.STRING => SString(term.asInstanceOf[IStrategoString].stringValue, getOrigin(term))
      case IStrategoTerm.LIST => SList(term.asInstanceOf[IStrategoList].asScala.toList.map(fromStratego(_)), getOrigin(term))
      case IStrategoTerm.TUPLE => STuple(term.asInstanceOf[IStrategoTuple].asScala.toList.map(fromStratego(_)), getOrigin(term))
      case IStrategoTerm.APPL => {
        val applTerm = term.asInstanceOf[IStrategoAppl]
        SCons(applTerm.getName, applTerm.asScala.toList.map(fromStratego(_)), getOrigin(term))
      }
    }
  }

  def getOrigin(term: IStrategoTerm): Option[Origin] = {
    val origin = ImploderAttachment.get(tryGetOrigin(term));
    if (origin == null)
      None
    else
      Some(Origin(
        origin.getLeftToken.getTokenizer.getFilename,
        origin.getLeftToken.getLine,
        origin.getLeftToken.getColumn,
        origin.getLeftToken.getStartOffset,
        origin.getRightToken.getEndOffset))
  }

  def toStratego(term: STerm)(implicit context: Context): IStrategoTerm = {
    val factory = context.getFactory
    val strategoTerm = term match {
      case SInt(i, o) => factory.makeInt(i)
      case SReal(d, o) => factory.makeReal(d)
      case SString(s, o) => factory.makeString(s)
      case SList(l, o) => factory.makeList(l.map(toStratego).asJava)
      case STuple(l, o) => factory.makeTuple(l.map(toStratego).toArray, factory.makeList())
      case SCons(c, l, o) => {
        val constr = factory.makeConstructor(c, l.length)
        val children = l.map(toStratego).toArray
        factory.makeAppl(constr, children, factory.makeList())
      }
    }
    term.o match {
      case Some(o) => strategoTerm.putAttachment(ImploderAttachment.createCompactPositionAttachment(o.filename, o.line, o.column, o.startOffset, o.endOffset))
      case None =>
    }
    strategoTerm
  }
}