package ao.learn.mst.example.rps

import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.{FiniteAction, RationalPlayer}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision, ExtensiveGameTerminal}
import scala.Predef._

//----------------------------------------------------------------------------------------------------------------------
object DuaneRockPaperScissorsNode {
  val root = DuaneRockPaperScissorsNodeFirst
}


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class DuaneRockPaperScissorsAction(
    index : Int,
    val value : Double)
      extends FiniteAction(index)

case object Rock     extends DuaneRockPaperScissorsAction(0, 3)
case object Paper    extends DuaneRockPaperScissorsAction(1, 1)
case object Scissors extends DuaneRockPaperScissorsAction(2, 2)
//case object Rock     extends DuaneRockPaperScissorsAction(0, 1)
//case object Paper    extends DuaneRockPaperScissorsAction(1, 1)
//case object Scissors extends DuaneRockPaperScissorsAction(2, 1)


//----------------------------------------------------------------------------------------------------------------------
case object EitherPlayerPov extends InformationSet

abstract class DuaneRockPaperScissorsNodeDecision extends ExtensiveGameDecision {
  def actions =
    Set(Rock, Paper, Scissors)

  def informationSet =
    EitherPlayerPov

  def child(action: FiniteAction) = action match {
    case Rock     => child(Rock)
    case Paper    => child(Paper)
    case Scissors => child(Scissors)
  }

  def child(action: DuaneRockPaperScissorsAction) : ExtensiveGameNode
}

//case object FirstPlayerPov extends InformationSet
case object DuaneRockPaperScissorsNodeFirst extends DuaneRockPaperScissorsNodeDecision {
  override def player =
    new RationalPlayer(0)

//  def informationSet = FirstPlayerPov

  def child(action: DuaneRockPaperScissorsAction) =
    new DuaneRockPaperScissorsNodeSecond(action)
}

//case object SecondPlayerPov extends InformationSet
case class DuaneRockPaperScissorsNodeSecond(
    firstPlayerAction : DuaneRockPaperScissorsAction)
      extends DuaneRockPaperScissorsNodeDecision
{
  override def player =
    new RationalPlayer(1)

//  def informationSet = SecondPlayerPov

  def child(action: DuaneRockPaperScissorsAction) =
   new DuaneRockPaperScissorsTerminal(firstPlayerAction, action)
}


//----------------------------------------------------------------------------------------------------------------------
class DuaneRockPaperScissorsTerminal(
    firstPlayerAction  : DuaneRockPaperScissorsAction,
    secondPlayerAction : DuaneRockPaperScissorsAction)
      extends ExtensiveGameTerminal
{
  def payoff = {
    val outcomes : (Double, Double) = {
      firstPlayerAction match {
        case Rock => secondPlayerAction match {
          case Rock     => (0, 0)
          case Paper    => (-Paper.value, Paper.value)
          case Scissors => (Rock.value, -Rock.value)
        }

        case Paper => secondPlayerAction match {
          case Rock     => (Paper.value, -Paper.value)
          case Paper    => (0, 0)
          case Scissors => (-Scissors.value, Scissors.value)
        }

        case Scissors => secondPlayerAction match {
          case Rock     => (-Rock.value, Rock.value)
          case Paper    => (Scissors.value, -Scissors.value)
          case Scissors => (0, 0)
        }
      }
    }

    ExpectedValue(
      Map(RationalPlayer(0) -> outcomes._1,
          RationalPlayer(1) -> outcomes._2))
  }
}