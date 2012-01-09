package ao.learn.mst.kuhn.adapt.v2

import ao.learn.mst.kuhn.card.KuhnCard._
import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.kuhn.card.{KuhnCardSequence, KuhnCard}
import ao.learn.mst.kuhn.state.{KuhnStake, KuhnState}
import ao.learn.mst.kuhn.action.KuhnActionSequence
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision, ExtensiveGameChance}
import ao.learn.mst.gen2.player.FiniteAction
import ao.learn.mst.kuhn.action.KuhnAction._

/**
 * Date: 03/12/11
 * Time: 8:03 PM
 */
class KuhnGameDecision(delegate : KuhnState)
    extends ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : Set[FiniteAction] = {
    val x : Set[FiniteAction] =
     Set( new FiniteAction(0) )
    
//    delegate.availableActions.map(_ => new FiniteAction(0))

    x
  }


  //--------------------------------------------------------------------------------------------------------------------
  def probabilities : ActionProbabilityMass =
  {
    val possibilities = actions

    val uniformProbability : Double =
      1.0 / possibilities.size

    val possibilityProbabilities = Map[FiniteAction, Double]() ++
      possibilities.map(_ -> uniformProbability)

    ActionProbabilityMass(
      possibilityProbabilities)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def child(outcome: FiniteAction) : ExtensiveGameDecision = {
    null
  }


  //--------------------------------------------------------------------------------------------------------------------
  case class Decision(
      delegate : KuhnAction)
        extends FiniteAction(delegate.id)
  {
    override def toString =
      delegate.toString
  }
}