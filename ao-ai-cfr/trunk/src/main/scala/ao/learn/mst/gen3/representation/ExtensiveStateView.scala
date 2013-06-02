package ao.learn.mst.gen3.representation

import ao.learn.mst.gen2.player.model.RationalPlayer
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.ExtensiveAction

/**
 * 01/06/13 8:52 PM
 */
trait ExtensiveStateView[I <: InformationSet, A <: ExtensiveAction]
{
  def viewer: RationalPlayer

  def informationSet: I

  def actions : Set[A]
}
