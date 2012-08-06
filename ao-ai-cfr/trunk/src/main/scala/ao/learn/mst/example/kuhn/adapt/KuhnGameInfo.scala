package ao.learn.mst.kuhn.adapt.v2

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.kuhn.card.KuhnCard._
import ao.learn.mst.kuhn.action.KuhnActionSequence._
import ao.learn.mst.kuhn.state.KuhnStake


case class KuhnGameInfo(
    playerCard: KuhnCard,
    actionSequence: KuhnActionSequence,
    stake: KuhnStake) extends InformationSet
{
  def contains(playerCard: KuhnCard, actionSequence: KuhnActionSequence): Boolean =
    this.playerCard == playerCard && this.actionSequence == actionSequence
}
