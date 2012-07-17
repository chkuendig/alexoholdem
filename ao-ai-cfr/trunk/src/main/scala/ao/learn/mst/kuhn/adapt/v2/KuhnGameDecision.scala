package ao.learn.mst.kuhn.adapt.v2

import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.kuhn.state.KuhnState
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision}
import ao.learn.mst.kuhn.action.KuhnAction._
import ao.learn.mst.gen2.player.{IndexedFiniteAction, FiniteAction}
import collection.immutable.{SortedSet, SortedMap}

/**
 * Date: 03/12/11
 * Time: 8:03 PM
 */
class KuhnGameDecision(delegate : KuhnState)
    extends ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : SortedSet[FiniteAction] = {
//    val x : Set[FiniteAction] =
//     Set( new FiniteAction(0) )
    
//    delegate.availableActions.map(_ => new FiniteAction(0))

//    x

    IndexedFiniteAction.sequence(2)
  }




  //--------------------------------------------------------------------------------------------------------------------
  def probabilities : ActionProbabilityMass =
  {
    val possibilities = actions

    val uniformProbability : Double =
      1.0 / possibilities.size

    val possibilityProbabilities = SortedMap[FiniteAction, Double]() ++
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