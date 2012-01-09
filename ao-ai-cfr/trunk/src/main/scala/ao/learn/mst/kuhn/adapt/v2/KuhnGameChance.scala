package ao.learn.mst.kuhn.adapt.v2

import ao.learn.mst.gen2.player.FiniteAction
import ao.learn.mst.kuhn.card.KuhnCard._
import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.kuhn.card.{KuhnCardSequence, KuhnCard}
import ao.learn.mst.kuhn.state.{KuhnStake, KuhnState}
import ao.learn.mst.kuhn.action.KuhnActionSequence
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision, ExtensiveGameChance}

/**
 * KuhnGameDecision
 *
 * Date: 03/12/11
 * Time: 8:03 PM
 */
object KuhnGameChance
    extends ExtensiveGameNode
{
  //--------------------------------------------------------------------------------------------------------------------
  def actions : Set[FiniteAction] = {
    val cardPermutations =
      for {
        firstCard  <- KuhnCard.values
        secondCard <- KuhnCard.values
        if secondCard != firstCard
      } yield (firstCard, secondCard)

    val possibilities =
      for {
        ((firstCard, secondCard), index)
        <- cardPermutations.zipWithIndex
      } yield
        Possibility(index, firstCard, secondCard)

    possibilities.toSet

//    ( for {
//        ((firstCard, secondCard), index)
//        <- cardPermutations.zipWithIndex
//      } yield
//        Possibility(index, firstCard, secondCard)
//    )(scala.collection.breakOut)
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
  case class Possibility(
      override val index : Int,

      firstPlayerCard  : KuhnCard,
      secondPlayerCard : KuhnCard)
        extends FiniteAction(index)
  {
    override def toString =
      firstPlayerCard + " / " + secondPlayerCard
  }
}