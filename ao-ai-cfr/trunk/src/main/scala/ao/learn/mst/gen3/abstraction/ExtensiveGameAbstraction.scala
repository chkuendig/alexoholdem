package ao.learn.mst.gen3.abstraction

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.player.model.FiniteAction
import ao.learn.mst.gen3.ExtensiveAction

/**
 * 11/05/13 5:09 PM
 */
trait ExtensiveGameAbstraction[I <: InformationSet, A <: ExtensiveAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def infoSetCount: Int

  def infoSetIndex(informationSet: I): Int

  def actionCount(informationSet: I): Int

  def actionIndex(informationSet: I, action: A): Int
}
