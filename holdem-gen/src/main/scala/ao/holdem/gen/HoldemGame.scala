package ao.holdem.gen

import ao.learn.mst.gen5.ExtensiveGame
import ao.holdem.engine.state.tree.StateTree
import ao.holdem.model.card.{Hole, Card, Community}
import ao.learn.mst.gen5.node._
import ao.util.math.stats.Combiner
import ao.holdem.model.Round
import ao.holdem.engine.state.HeadsUpStatus
import ao.learn.mst.gen5.node.Chance
import scala.Some
import ao.holdem.engine.eval.EvalBy5
import scala.collection.JavaConversions._


/**
 * 09/02/14 7:41 PM
 */
object HoldemGame
    extends ExtensiveGame[HoldemState, HoldemInfo, HoldemAction]
{
  val playerCount: Int =
    2

  val initialState: HoldemState =
    HoldemState(
      StateTree.headsUpRoot(),
      Seq.empty,
      Community.PREFLOP)


  //--------------------------------------------------------------------------------------------------------------------
  def node(state: HoldemState): ExtensiveNode[HoldemInfo, HoldemAction] =
  {
    chance(state) match {
      case Some(chance) => chance
      case _ =>
        if (state.actionSequence.status() == HeadsUpStatus.IN_PROGRESS) {
          decision(state)
        } else {
          terminal(state)
        }
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def chance(state: HoldemState): Option[Chance[HoldemInfo, HoldemAction]] = {
    val round: Round =
      state.actionSequence.round()

    val chances: Traversable[HoldemAction] =
      if (state.holes.length < playerCount) {
        val permutations: Iterable[Array[Card]] =
          new Combiner(state.deck.toArray, 2)

        val holeChances: Traversable[HoleChance] =
          permutations.map(hole => HoleChance(hole(0), hole(1)))

        holeChances
      } else if (round == Round.FLOP && ! state.community.hasFlop) {
        val permutations: Iterable[Array[Card]] =
          new Combiner(state.deck.toArray, 3)

        val flopChances: Traversable[FlopChance] =
          permutations.map(flop => FlopChance(flop(0), flop(1), flop(2)))

        flopChances
      } else if (round == Round.TURN && ! state.community.hasTurn) {
        state.deck.map(TurnChance)
      } else if (round == Round.RIVER && ! state.community.hasRiver) {
        state.deck.map(RiverChance)
      } else {
        None
      }

    if (chances.isEmpty) {
      None
    } else {
      Some(Chance(Outcome.equalProbability(chances)))
    }
  }


  //--------------------------------------------------------------------------------------------------------------------
  def decision(state: HoldemState): Decision[HoldemInfo, HoldemAction] =
  {
    val nextToAct: Int =
      if (state.actionSequence.dealerIsNext()) 1 else 0

    Decision(
      nextToAct,
      HoldemInfo(state.actionSequence, state.holes(nextToAct), state.community),
      state.actionSequence.acts().keySet().map(DecisionAction))
  }


  //--------------------------------------------------------------------------------------------------------------------
  def terminal(state: HoldemState): Terminal[HoldemInfo, HoldemAction] =
  {
    val payoffs: Seq[Double] =
      state.actionSequence.status match {
        case HeadsUpStatus.SHOWDOWN =>
          val winners: Set[Int] = {
            val handStrengths: Seq[Short] =
              (0 until playerCount).map(player => {
                val cards: Array[Card] =
                  state.holes(player).toArray() ++
                    state.community.toArray()

                EvalBy5.valueOf(cards: _*)
              })

            val winningHandStrength: Short =
              handStrengths.max

            handStrengths
              .zipWithIndex
              .filter(_._1 == winningHandStrength)
              .map(_._2)
              .toSet
          }

          (0 until playerCount).map((player: Int) => {
            if (winners.contains(player)) {
              state.actionSequence.stakes.toDouble
            } else {
              -state.actionSequence.stakes.toDouble
            }
          })

        case HeadsUpStatus.DEALER_WINS =>
          Seq(
            -state.actionSequence.dealeeCommit,
            state.actionSequence.dealeeCommit)

        case HeadsUpStatus.DEALEE_WINS =>
          Seq(
            state.actionSequence.dealerCommit,
            -state.actionSequence.dealerCommit)

        case _ => throw new Error
      }

    Terminal(payoffs)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def transition(nonTerminal: HoldemState, action: HoldemAction): HoldemState = {
    action match {
      case DecisionAction(choice) =>
        HoldemState(
          nonTerminal.actionSequence.acts().get(choice),
          nonTerminal.holes,
          nonTerminal.community)

      case HoleChance(a, b) =>
        HoldemState(
          nonTerminal.actionSequence,
          nonTerminal.holes :+ Hole.valueOf(a, b),
          nonTerminal.community)

      case FlopChance(a, b, c) =>
        HoldemState(
          nonTerminal.actionSequence,
          nonTerminal.holes,
          new Community(a, b, c))

      case TurnChance(turn) =>
        HoldemState(
          nonTerminal.actionSequence,
          nonTerminal.holes,
          nonTerminal.community.addTurn(turn))

      case RiverChance(river) =>
        HoldemState(
          nonTerminal.actionSequence,
          nonTerminal.holes,
          nonTerminal.community.addRiver(river))
    }
  }
}
