package ao.learn.mst.example.prs

import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.{FiniteAction, RationalPlayer}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.game.{ExtensiveGameNode, ExtensiveGameDecision, ExtensiveGameTerminal}
import scala.Predef._

//----------------------------------------------------------------------------------------------------------------------
object DuanePaperRockScissorsNode {
  val root = DuanePaperRockScissorsNodeFirst
}


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class DuanePaperRockScissorsAction(
    index : Int,
    val value : Double)
      extends FiniteAction(index)

case object Rock     extends DuanePaperRockScissorsAction(0, 3)
case object Paper    extends DuanePaperRockScissorsAction(1, 2)
case object Scissors extends DuanePaperRockScissorsAction(2, 1)
//case object Rock     extends DuanePaperRockScissorsAction(0, 1)
//case object Paper    extends DuanePaperRockScissorsAction(1, 1)
//case object Scissors extends DuanePaperRockScissorsAction(2, 1)


//----------------------------------------------------------------------------------------------------------------------
case object SingletonInfoSet extends InformationSet

abstract class DuanePaperRockScissorsNodeDecision extends ExtensiveGameDecision {
  def actions =
    Set(Rock, Paper, Scissors)

  def informationSet = SingletonInfoSet

  def child(action: FiniteAction) = action match {
    case Rock     => child(Rock)
    case Paper    => child(Paper)
    case Scissors => child(Scissors)
  }

  def child(action: DuanePaperRockScissorsAction) : ExtensiveGameNode
}

object DuanePaperRockScissorsNodeFirst extends DuanePaperRockScissorsNodeDecision {
  override def player =
    new RationalPlayer(0)

  def child(action: DuanePaperRockScissorsAction) =
    new DuanePaperRockScissorsNodeSecond(action)
}

case class DuanePaperRockScissorsNodeSecond(
    firstPlayerAction : DuanePaperRockScissorsAction)
      extends DuanePaperRockScissorsNodeDecision
{
  override def player =
    new RationalPlayer(1)

  def child(action: DuanePaperRockScissorsAction) =
   new DuanePaperRockScissorsTerminal(firstPlayerAction, action)
}


//----------------------------------------------------------------------------------------------------------------------
class DuanePaperRockScissorsTerminal(
    firstPlayerAction  : DuanePaperRockScissorsAction,
    secondPlayerAction : DuanePaperRockScissorsAction)
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