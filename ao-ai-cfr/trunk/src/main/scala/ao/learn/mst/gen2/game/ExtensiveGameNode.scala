package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.player.FiniteAction

/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 */
trait ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : Set[FiniteAction]
}