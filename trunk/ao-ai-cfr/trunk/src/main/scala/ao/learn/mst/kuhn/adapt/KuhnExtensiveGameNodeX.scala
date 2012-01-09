package ao.learn.mst.kuhn.adapt

import ao.learn.mst.gen.ExtensiveGameNodeX
import ao.learn.mst.gen.chance.ProbabilityMass
import ao.learn.mst.gen.history.{ExtensiveHistory, ExtensiveInformationPartition}
import ao.learn.mst.gen.act.{ExtensiveActionObservation, ExtensiveAction}
import ao.learn.mst.kuhn.card.KuhnCardSequence
import ao.learn.mst.kuhn.card.KuhnCard
import ao.learn.mst.kuhn.card.KuhnCard._
import ao.learn.mst.kuhn.state.{KuhnStake, KuhnState}
import ao.learn.mst.gen.utility.ExtensivePayoff
import ao.learn.mst.gen.player.{Rational, ExtensivePlayerPartition, ExtensivePlayerPosition, Chance}
import ao.learn.mst.kuhn.state.KuhnPosition._
import ao.learn.mst.kuhn.action.{KuhnAction, KuhnActionSequence}


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class KuhnExtensiveGameNodeX(val delegate : KuhnState)
    extends ExtensiveGameNodeX

object KuhnExtensiveGameNodeX
{
  def addEvent(
      events : Seq[ExtensiveInformationPartition],
      index  : Int,
      event  : ExtensiveActionObservation) : Seq[ExtensiveInformationPartition] =
  {
    events.zipWithIndex.map(
      e => if (e._2 == index) e._1.addEvent(event) else e._1)
  }
}
import KuhnExtensiveGameNodeX._


//----------------------------------------------------------------------------------------------------------------------
trait KuhnChanceNodeX
    extends ExtensiveGameNodeX
{
  def payoff = None

  def visibleToIndexPlayer : Int
  def observableOnlyTo = Some(ExtensivePlayerPosition( visibleToIndexPlayer ))

  def playerPartition = Some(Chance)

  def childProbabilities = Some(ProbabilityMass(
    Seq.fill(childCount)(1.0/childCount)))

  def childUnchecked(index: ExtensiveAction) : ExtensiveGameNodeX
  def child(index: ExtensiveAction) = {
    assert(0 <= index.relativeIndex &&
                index.relativeIndex < childCount)

    Some(childUnchecked( index ))
  }
}

object FirstPlayerCardDeal
    extends KuhnChanceNodeX
{
  def childCount = 3

  def visibleToIndexPlayer = 0

  def informationPartition = Seq.fill(2)(
    ExtensiveInformationPartition(ExtensiveHistory(Seq.empty)))

  def childUnchecked(index: ExtensiveAction) =
    new LastPlayerCardDeal( index, informationPartition )
}

class LastPlayerCardDeal(
    firstPlayerHand            : ExtensiveAction,
    parentInformationPartition : Seq[ExtensiveInformationPartition])
    extends KuhnChanceNodeX
{
  def childCount = 2

  def visibleToIndexPlayer = 1

  def informationPartition =
    addEvent(parentInformationPartition, 0,
      ExtensiveActionObservation(Chance, firstPlayerHand))
//    Seq(parentInformationPartition(0).addEvent(
//          ExtensiveActionObservation(Chance, firstPlayerHand)),
//        parentInformationPartition(1))
//    Seq(ExtensiveInformationPartition(ExtensiveHistory(
//          Seq(ExtensiveActionObservation(Chance, firstPlayerHand)))),
//        ExtensiveInformationPartition(ExtensiveHistory(Seq.empty)))

  def childUnchecked(index: ExtensiveAction) = {
    val cardsByOrdinal = KuhnCard.values.toList

    new PostDealNode(
      new KuhnState(
        KuhnCardSequence(
          cardsByOrdinal( firstPlayerHand.relativeIndex ),
          cardsByOrdinal( index          .relativeIndex ))),
      addEvent(informationPartition, 1,
        ExtensiveActionObservation(Chance, index)))
  }
}


//----------------------------------------------------------------------------------------------------------------------
class PostDealNode(
      delegate                 : KuhnState,
      val informationPartition : Seq[ExtensiveInformationPartition])
    extends KuhnExtensiveGameNodeX(delegate)
{
  //--------------------------------------------------------------------------------------------------------------------
  def childCount = delegate.availableActions.size

  def payoff = delegate.winner.flatMap(winner => {
    val kuhnOutcome = delegate.stake.toOutcome(winner)

    Some(ExtensivePayoff(
      Seq(kuhnOutcome.firstPlayerDelta.toDouble,
          kuhnOutcome.lastPlayerDelta .toDouble)))
  })

  def playerPartition = delegate.nextToAct.flatMap(position =>
    Some(Rational(ExtensivePlayerPosition( position.id ))))

  def childProbabilities = None

  def observableOnlyTo = None

  def child(index: ExtensiveAction) = {
    val someNextToActIndex = nextToActIndex.get

    val observation = ExtensiveActionObservation(
      Rational(ExtensivePlayerPosition(someNextToActIndex)), index)

    Some(new PostDealNode(
      delegate.act( KuhnAction.values.toSeq(index.relativeIndex) ),
      addEvent(informationPartition, someNextToActIndex, observation)))
  }
}
