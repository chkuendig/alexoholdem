package ao.learn.mst.gen2.info.set

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.player.model.RationalPlayer

/**
 * 28/04/13 11:55 AM
 */
trait PlayerInformationSet
    extends InformationSet
{
  def nextToAct:RationalPlayer
}
