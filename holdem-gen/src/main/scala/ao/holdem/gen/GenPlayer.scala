package ao.holdem.gen

import ao.holdem.bot.AbstractPlayer
import ao.holdem.bot.regret.HoldemAbstraction
import ao.learn.mst.gen5.state.strategy.StrategyAccumulator
import ao.holdem.engine.state.State
import ao.holdem.model.card.sequence.CardSequence
import ao.holdem.model.act.{AbstractAction, Action}
import ao.holdem.engine.state.tree.StateTree
import ao.holdem.model.{ChipStack, Avatar}
import ao.holdem.gen.abs.BucketAbstraction
import scala.util.Random
import ao.learn.mst.gen5.state.strategy.impl.AverageStrategy
import ao.learn.mst.lib.DisplayUtils

/**
 *
 */
class GenPlayer(
    holdemAbstraction: HoldemAbstraction,
    strategyAccumulator: StrategyAccumulator,
    display: Boolean = false,
    randomness: Random = new Random)
    extends AbstractPlayer
{
  private val bucketAbstraction =
    BucketAbstraction(holdemAbstraction)

  private val averageStrategy =
    new AverageStrategy(strategyAccumulator)

  private var prevNode: StateTree.Node = StateTree.headsUpRoot
  private var prevCards: Option[CardSequence] = None


  private val displayInfo = true


  override def handEnded(deltas: java.util.List[ChipStack]) {
    prevNode = StateTree.headsUpRoot

    if (displayInfo) {
      println(s"Bot hole cards: ${prevCards.get.hole()}")
    }
  }


  override def act(state: State, cards: CardSequence): Action = {
    require(state.seats.length == 2, "Only works in heads-up mode")

    var gamePath: StateTree.Node = StateTree.fromState(prevNode, state, 4)
    if (gamePath == null) {
      gamePath = StateTree.fromState(state)
      if (gamePath == null) {
        throw new Error(s"Out of path: $state")
      }
    }

    val info = HoldemInfo(gamePath, cards.hole(), cards.community())
    val infoIndex: Long = bucketAbstraction.informationSetIndex(info)

    val actionCount = state.actions(false).size
    val strategy: IndexedSeq[Double] =
      averageStrategy.probabilities(infoIndex, actionCount)

    if (display) {
      if (prevNode == StateTree.headsUpRoot) {
        println(s"bot cards: ${cards.hole}")
      }

      println(s"bot strategy: ${DisplayUtils.displayProbabilities(strategy)}")
    }

    val selectedIndex: Int =
      strategy
        .zipWithIndex
        .map((si: (Double, Int)) => (si._1 * randomness.nextDouble(), si._2))
        .maxBy(_._1)
        ._2

    val act = AbstractAction.VALUES(selectedIndex)
    val realAction = state.reify(act.toFallbackAction)

    prevCards = Some(cards)
    if (displayInfo) {
      println(s"Bot action: $realAction")
    }

    prevNode = gamePath
    realAction
  }
}
