package ao.holdem.gen

import ao.holdem.bot.AbstractPlayer
import ao.holdem.bot.regret.HoldemAbstraction
import ao.learn.mst.gen5.state.strategy.{StrategyAccumulator, StrategyStore}
import ao.holdem.engine.state.State
import ao.holdem.model.card.sequence.CardSequence
import ao.holdem.model.act.{AbstractAction, FallbackAction, Action}
import ao.holdem.engine.state.tree.StateTree
import ao.holdem.model.{Round, ChipStack, Avatar}
import ao.holdem.canon.hole.CanonHole
import ao.holdem.canon.flop.Flop
import ao.holdem.canon.turn.Turn
import ao.holdem.bot.limit_cfr.CfrBot2
import ao.holdem.canon.river.River
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


  private var isNewGame: Boolean = true

  private var prevNode: StateTree.Node = StateTree.headsUpRoot

//  private var canonHole: Option[CanonHole] = None
//  private var canonFlop: Option[Flop] = None
//  private var canonTurn: Option[Turn] = None
//  private var prevRound: Option[Round] = None
//
//  private var prevCards: Option[CardSequence] = None
//
//  private var handProbabilities: Seq[String] = Seq()
//
//  private var roundBucket: Char = 0
//  private var holeBucket: Int = 0
//  private var flopBucket: Int = 0
//  private var turnBucket: Int = 0


  override def handEnded(deltas: java.util.Map[Avatar, ChipStack]) {
    isNewGame = true

//    prevNode = StateTree.headsUpRoot
//
//    resetRoundCanons()
//
//    if (display) {
//      val actualCards = prevCards.get
//
//      println(s"bot shows cards: $actualCards   ${CfrBot2.handType(prevCards)}")
//      println(s"thinking: $handProbabilities")
//      handProbabilities = Seq()
//    }
  }
//
//  private def resetRoundCanons(): Unit = {
//    canonHole = None
//    canonFlop = None
//    canonTurn = None
//    prevRound = None
//  }



  override def act(state: State, cards: CardSequence): Action = {
    require(state.seats.length == 2, "Only works in heads-up mode")

//    if (prevRound == Some(state.round)) {
//      if (state.round == Round.PREFLOP) {
//        handProbabilities = Seq()
//      } else {
//        handProbabilities += state.round.toString.substring(0, 1)
//      }
//    }
//
    var gamePath: StateTree.Node = StateTree.fromState(prevNode, state, 4)
    if (gamePath == null) {
      gamePath = StateTree.fromState(state)
      if (gamePath == null) {
        throw new Error("Out of path")
//        println("out of game path")
//        return state.reify(FallbackAction.CHECK_OR_CALL)
      }
//      resetRoundCanons()
    }
    prevNode = gamePath

//    findBucket(cards, state.round)

    val info = HoldemInfo(gamePath, cards.hole(), cards.community())
    val infoIndex = bucketAbstraction.informationSetIndex(info)

    val actionCount = state.actions(false).size
    val strategy: IndexedSeq[Double] =
      averageStrategy.probabilities(infoIndex, actionCount)

    if (display) {
      if (isNewGame) {
        println(s"bot cards: ${cards.hole}")
        isNewGame = false
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

    realAction
  }

//  private def findBucket(cards: CardSequence, round: Round) {
//    if (prevRound == Some(round)) return
//
//    val absTree = holdemAbstraction.tree(true)
//    if (canonHole.isEmpty) {
//      val realHole = CanonHole.create(cards.hole)
//      canonHole = Some(realHole)
//      holeBucket = absTree.getHole(realHole.canonIndex)
//    }
//
//    if (round == Round.PREFLOP) {
//      roundBucket = holeBucket.asInstanceOf[Char]
//      return
//    }
//
//    if (canonFlop.isEmpty) {
//      canonFlop = Some(canonHole.get.addFlop(cards.community))
//      flopBucket = absTree.getFlop(canonFlop.get.canonIndex)
//    }
//
//    val absDecoder = holdemAbstraction.decoder
//    if (round == Round.FLOP) {
//      roundBucket = absDecoder.decode(holeBucket, flopBucket)
//      return
//    }
//
//    if (canonTurn.isEmpty) {
//      canonTurn = Some(canonFlop.get.addTurn(cards.community.turn))
//      turnBucket = absTree.getTurn(canonTurn.get.canonIndex)
//    }
//
//    if (round == Round.TURN) {
//      roundBucket = absDecoder.decode(holeBucket, flopBucket, turnBucket)
//      return
//    }
//
//    val canonRiver: River = canonTurn.get.addRiver(cards.community.river)
//    val riverBucket: Int = absTree.getRiver(canonRiver.canonIndex)
//    roundBucket = absDecoder.decode(holeBucket, flopBucket, turnBucket, riverBucket)
//  }
}
