package ao.learn.mst.example.ocp.adapt

import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision}
import ao.learn.mst.gen2.player.{RationalPlayer, IndexedFiniteAction, FiniteAction}
import collection.immutable.SortedSet
import ao.learn.mst.example.kuhn.action.KuhnAction
import ao.learn.mst.example.ocp.state.OcpState
import ao.learn.mst.example.ocp.action.OcpAction

/**
 * Date: 03/12/11
 * Time: 8:03 PM
 */
case class OcpGameDecision(delegate: OcpState)
  extends ExtensiveGameDecision
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions: SortedSet[FiniteAction] = {
    IndexedFiniteAction.sequence(
      delegate.availableActions.length)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def child(action: FiniteAction): ExtensiveGameNode = {
    val childDelegate = delegate.act(OcpAction.values.toSeq(action.index))

    childDelegate.winner match {
      case None => OcpGameDecision(childDelegate)
      case Some(_) => OcpGameTerminal(childDelegate)
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  override def player =
    RationalPlayer(delegate.nextToAct.get.id)

  def informationSet =
    OcpGameInfo(
      delegate.cards.forPlayer(player.index),
      delegate.actions,
      delegate.stake)
}