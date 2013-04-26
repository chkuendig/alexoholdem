package ao.learn.mst.gen2.player

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * 25/04/13 8:02 PM
 */
trait ExtensiveGamePlayer
{
  def rankActions(node:ExtensiveGameDecision):Seq[FiniteAction]
}
