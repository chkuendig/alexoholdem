package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.gen2.player.FiniteAction

/**
 * Date: 03/12/11
 * Time: 7:27 PM
 */

trait ExtensiveGameChance
    extends ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def probabilities : ActionProbabilityMass


  //--------------------------------------------------------------------------------------------------------------------
  def child(outcome : FiniteAction) : ExtensiveGameDecision
}