package ao.learn.mst.gen2.info

import ao.learn.mst.gen2.player.{RationalPlayer, FiniteAction}

/**
 *
 */

trait InformationSetIndex
{
  //--------------------------------------------------------------------------------------------------------------------
  def informationSetCount: Int

  def indexOf(informationSet: InformationSet): Int

  def actionsOf(informationSet: InformationSet): Set[FiniteAction]

  def informationSets: Traversable[InformationSet]

  def nextToAct(informationSet: InformationSet): RationalPlayer
}
