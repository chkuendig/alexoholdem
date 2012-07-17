package ao.learn.mst.example.incomplete

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.prob.ActionProbabilityMass
import ao.learn.mst.gen2.game.{ExtensiveGameDecision, ExtensiveGameChance, ExtensiveGameTerminal}
import ao.learn.mst.gen2.player.{NamedFiniteAction, RationalPlayer, FiniteAction}
import scala.Predef._
import collection.immutable.{SortedSet, SortedMap}


//----------------------------------------------------------------------------------------------------------------------
/**
 * Game tree for first game at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Incomplete_information
 */
object IncompleteNode {
  val root = IncompleteChance
}


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class IncompleteType(index : Int, name : String)
  extends NamedFiniteAction(index, name)

case object Type1 extends IncompleteType(0, "t1")
case object Type2 extends IncompleteType(1, "t2")


//----------------------------------------------------------------------------------------------------------------------
sealed abstract class IncompleteAction(index : Int)
    extends FiniteAction(index)

case object Up extends IncompleteAction(0)
case object Down extends IncompleteAction(1)


//----------------------------------------------------------------------------------------------------------------------
case object IncompleteChance extends ExtensiveGameChance {
  def actions =
    SortedSet[FiniteAction](
      Type1, Type2)

  def probabilityMass =
    ActionProbabilityMass(
      SortedMap[FiniteAction, Double]() ++
        actions.map((_ -> 1.0/actions.size)))

  override def child(action: FiniteAction) =
    action.index match {
      case Type1.index => IncompletePlayerOneTypeOne
      case Type2.index => IncompletePlayerOneTypeTwo
    }
}


//----------------------------------------------------------------------------------------------------------------------
abstract class IncompleteDecision extends ExtensiveGameDecision {
  def actions =
    SortedSet[FiniteAction](
      Up, Down)
}

abstract class IncompleteOnePlayerType extends IncompleteDecision {
  override def player =
    RationalPlayer(0)
}


case object PlayerOneTypeOnePov extends InformationSet
case object IncompletePlayerOneTypeOne extends IncompleteOnePlayerType
{
  def informationSet =
    PlayerOneTypeOnePov

  def child(action: FiniteAction) =
    action.index match {
      case Up.index => IncompletePlayerTwoAfterUp(Type1)
      case Down.index => IncompletePlayerTwoAfterDown(Type1)
    }
}


case object PlayerOneTypeTwoPov extends InformationSet
case object IncompletePlayerOneTypeTwo extends IncompleteOnePlayerType
{
  def informationSet =
    PlayerOneTypeTwoPov

  def child(action: FiniteAction) =
    action.index match {
      case Up.index => IncompletePlayerTwoAfterUp(Type2)
      case Down.index => IncompletePlayerTwoAfterDown(Type2)
    }
}



//----------------------------------------------------------------------------------------------------------------------
abstract class IncompletePlayerTwo(playerOneType : IncompleteType) extends IncompleteDecision
{
  override def player =
    RationalPlayer(1)
}


case object PlayerTwoAfterUpPov extends InformationSet

case class IncompletePlayerTwoAfterUp(
    playerOneType : IncompleteType)
      extends IncompletePlayerTwo(playerOneType)
{
  def informationSet =
    PlayerTwoAfterUpPov

  def child(action: FiniteAction) =
    action.index match {
      case Up.index   => IncompleteTerminal(playerOneType, Up, Up)
      case Down.index => IncompleteTerminal(playerOneType, Up, Down)
    }
}


case object PlayerTwoAfterDownPov extends InformationSet

case class IncompletePlayerTwoAfterDown(
    playerOneType : IncompleteType)
      extends IncompletePlayerTwo(playerOneType)
{
  def informationSet = PlayerTwoAfterDownPov

  def child(action: FiniteAction) =
    action.index match {
      case Up.index   => IncompleteTerminal(playerOneType, Down, Up)
      case Down.index => IncompleteTerminal(playerOneType, Down, Down)
    }
}


//----------------------------------------------------------------------------------------------------------------------
case class IncompleteTerminal(
    playerOneType   : IncompleteType,
    playerOneAction : IncompleteAction,
    playerTwoAction : IncompleteAction)
      extends ExtensiveGameTerminal
{
  def payoff = {
    val outcomes : (Double, Double) = {
      playerOneType match {
        case Type1 => playerOneAction match {
          case Up => playerTwoAction match {
            case Up   => (0, 0)
            case Down => (2, 1)
          }
          case Down => playerTwoAction match {
            case Up   => (1, 2)
            case Down => (3, 1)
          }
        }

        case Type2 => playerOneAction match {
          case Up => playerTwoAction match {
            case Up   => (2, 1)
            case Down => (3, 1)
          }
          case Down => playerTwoAction match {
            case Up   => (0, 0)
            case Down => (1, 2)
          }
        }
      }
    }

    ExpectedValue(Map(
        RationalPlayer(0) -> outcomes._1,
        RationalPlayer(1) -> outcomes._2))
  }
}

