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

sealed abstract class Term {
  val o: Option[Origin]
  //  def matchInt: Option[scala.Int] = {
  //    this match {
  //      case Int(i) => Some(i)
  //      case _ => None
  //    }
  //  }
  //  
  //  def matchReal: Option[Double] = {
  //    this match {
  //      case Real(d) => Some(d)
  //      case _ => None
  //    }
  //  }
  //  
  //  def matchString: Option[java.lang.String] = {
  //    this match {
  //      case String(s, o) => Some(s)
  //      case _ => None
  //    }
  //  }
}

case class Int(i: scala.Int, o: Option[Origin]) extends Term
case class Real(d: Double, o: Option[Origin]) extends Term
case class String(s: java.lang.String, o: Option[Origin]) extends Term
case class List(l: scala.collection.immutable.List[Term], o: Option[Origin]) extends Term
case class Tuple(l: scala.collection.immutable.List[Term], o: Option[Origin]) extends Term
case class Cons(c: java.lang.String, l: scala.collection.immutable.List[Term], o: Option[Origin]) extends Term

case class Origin(
  filename: java.lang.String,
  line: scala.Int,
  column: scala.Int,
  startOffset: scala.Int,
  endOffset: scala.Int)

object Term {
  def fromStratego(term: IStrategoTerm): Term = {
    term.getTermType match {
      case IStrategoTerm.INT => Int(term.asInstanceOf[IStrategoInt].intValue, getOrigin(term))
      case IStrategoTerm.REAL => Real(term.asInstanceOf[IStrategoReal].realValue, getOrigin(term))
      case IStrategoTerm.STRING => String(term.asInstanceOf[IStrategoString].stringValue, getOrigin(term))
      case IStrategoTerm.LIST => List(term.asInstanceOf[IStrategoList].asScala.toList.map(fromStratego(_)), getOrigin(term))
      case IStrategoTerm.TUPLE => Tuple(term.asInstanceOf[IStrategoTuple].asScala.toList.map(fromStratego(_)), getOrigin(term))
      case IStrategoTerm.APPL => {
        val applTerm = term.asInstanceOf[IStrategoAppl]
        Cons(applTerm.getName, applTerm.asScala.toList.map(fromStratego(_)), getOrigin(term))
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

  def toStratego(term: Term)(implicit context: Context): IStrategoTerm = {
    val factory = context.getFactory
    val strategoTerm = term match {
      case Int(i, o) => factory.makeInt(i)
      case Real(d, o) => factory.makeReal(d)
      case String(s, o) => factory.makeString(s)
      case List(l, o) => factory.makeList(l.map(toStratego(_)).asJava)
      case Tuple(l, o) => factory.makeTuple(l.map(toStratego(_)).toArray, factory.makeList())
      case Cons(c, l, o) => {
        val constr = factory.makeConstructor(c, l.length)
        val children = l.map(toStratego(_)).toArray
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