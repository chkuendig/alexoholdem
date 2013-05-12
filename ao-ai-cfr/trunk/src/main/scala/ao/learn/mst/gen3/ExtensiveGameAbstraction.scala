package ao.learn.mst.gen3

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * 11/05/13 5:09 PM
 */
trait ExtensiveGameAbstraction[I <: InformationSet, A <: FiniteAction]
{
  //--------------------------------------------------------------------------------------------------------------------
  def infoSetCount: Int

  def infoSetIndex(informationSet: I): Int

  def actionCount(informationSet: I): Int

  def actionIndex(informationSet: I, action: A): Int
}
