package ao.learn.mst.example.kuhn.adapt

import ao.learn.mst.example.kuhn.card.KuhnCard._
import ao.learn.mst.example.kuhn.action.KuhnActionSequence._
import ao.learn.mst.example.kuhn.state.KuhnState
import ao.learn.mst.gen2.info.set.PlayerInformationSet
import ao.learn.mst.gen2.player.model.RationalPlayer


case class KuhnGameInfo(
    playerCard: KuhnCard,
    state: KuhnState
    /*actionSequence: KuhnActionSequence,
    stake: KuhnStake*/) extends PlayerInformationSet
{
  def contains(playerCard: KuhnCard, actionSequence: KuhnActionSequence): Boolean =
    this.playerCard == playerCard && this.state.actions == actionSequence

  def nextToAct: RationalPlayer =
    RationalPlayer(state.nextToAct.get.id)
}
