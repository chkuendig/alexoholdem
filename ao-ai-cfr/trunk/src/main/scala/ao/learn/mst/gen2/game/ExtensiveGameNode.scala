package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen.utility.ExtensivePayoff
import ao.learn.mst.gen2.player.FiniteAction

/**
 * http://en.wikipedia.org/wiki/Extensive-form_game
 */
trait ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : Set[FiniteAction]
}