package org.kotgll.rsm.stringinput.sppf

import java.util.*

open class ParentSPPFNode(leftExtent : Int, rightExtent : Int) : SPPFNode(leftExtent, rightExtent)
{
  val kids : HashSet<PackedSPPFNode> = HashSet()

  override fun toString() = "ParentSPPFNode(leftExtent=$leftExtent, rightExtent=$rightExtent)"

  override fun equals(other : Any?) : Boolean
  {
    if (other !is ParentSPPFNode) return false

    return super.equals(other)
  }

  override val hashCode : Int = Objects.hash(leftExtent, rightExtent)
  override fun hashCode() = hashCode
}
