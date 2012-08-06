package ao.learn.mst.kuhn.adapt.v2

import ao.learn.mst.kuhn.state.{KuhnStake, KuhnState}
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision}
import ao.learn.mst.kuhn.action.KuhnActionSequence._
import ao.learn.mst.gen2.player.{RationalPlayer, IndexedFiniteAction, FiniteAction}
import collection.immutable.SortedSet
import ao.learn.mst.gen2.info.ValueInformationSet
import ao.learn.mst.kuhn.action.KuhnAction
import ao.learn.mst.kuhn.card.KuhnCard._

/**
 * Date: 03/12/11
 * Time: 8:03 PM
 */
case class KuhnGameDecision(delegate : KuhnState)
    extends ExtensiveGameDecision
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : SortedSet[FiniteAction] = {
    IndexedFiniteAction.sequence(
      delegate.availableActions.length)
  }


  //--------------------------------------------------------------------------------------------------------------------
//  def probabilities : ActionProbabilityMass =
//  {
//    val possibilities = actions
//
//    val uniformProbability : Double =
//      1.0 / possibilities.size
//
//    val possibilityProbabilities = SortedMap[FiniteAction, Double]() ++
//      possibilities.map(_ -> uniformProbability)
//
//    ActionProbabilityMass(
//      possibilityProbabilities)
//  }


  //--------------------------------------------------------------------------------------------------------------------
  def child(action: FiniteAction) : ExtensiveGameNode = {
    val childDelegate = delegate.act(KuhnAction.values.toSeq(action.index))

    childDelegate.winner match {
      case None => KuhnGameDecision(childDelegate)
      case Some(_) => KuhnGameTerminal(childDelegate)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def player =
    RationalPlayer(delegate.nextToAct.get.id)

  def informationSet =
    KuhnGameInfo(
      delegate.cards.forPlayer( player.index ),
      delegate.actions,
      delegate.stake)
}