package org.example

case class MDef(name: String)
case class MRef(name: String)

case class Model(
  defs: List[Def],
  refs: List[Ref],
  defOrigins: Map[MDef, Origin],
  refOrigins: Map[MRef, Origin],
  originRefs: Map[Origin, MRef])

object Model {

  def buildModel(ids: IDs): Model = {
    val defs = ids.defsRefs.collect { case a: Def => a }
    val refs = ids.defsRefs.collect { case a: Ref => a }
    val defOrigins = defs.map(d => MDef(d.id) -> d.o).collect { case (a, Some(b)) => (a, b) }.toMap
    val originRefs = refs.map(d => d.o -> MRef(d.id) ).collect { case (Some(a), b) => (a, b) }.toMap
    val refOrigins = originRefs.map(_.swap)
    Model(defs, refs, defOrigins, refOrigins, originRefs)
  }

  def getErrors(m: Model): List[EditorMessage] = {
    duplicateDefinitions(m) ++ unresolvedReferences(m)
  }

  def duplicateDefinitions(m: Model): List[EditorMessage] = {
    val duplicateDefs = m.defs.groupBy(_.id).filter(_._2.size > 1).flatMap(_._2).map { d => EditorMessage(d.o.get, "Duplicate definition: " + d.id) }
    duplicateDefs.toList
  }

  def unresolvedReferences(m: Model): List[EditorMessage] = {
    val unresolvedReferences = m.refs.filterNot { r => m.defOrigins.contains(MDef(r.id)) }.map { d => EditorMessage(d.o.get, "Unresolved Reference: " + d.id) }
    unresolvedReferences
  }

  def resolveReference(m: Model, ref: Origin): Option[Origin] = {
    val mref = m.originRefs.get(ref)
    val mdef = mref.map { case MRef(id) => MDef(id) }
    val defOrigin = mdef.flatMap { d => m.defOrigins.get(d) }
    defOrigin
  }

}