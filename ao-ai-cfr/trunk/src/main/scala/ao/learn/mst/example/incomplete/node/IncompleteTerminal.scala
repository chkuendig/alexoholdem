package ao.learn.mst.example.incomplete.node

import ao.learn.mst.gen2.game.ExtensiveGameTerminal
import ao.learn.mst.example.incomplete._
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.RationalPlayer


//----------------------------------------------------------------------------------------------------------------------
case class IncompleteTerminal(
      playerOneType: IncompleteType,
      playerOneAction: IncompleteAction,
      playerTwoAction: IncompleteAction)
    extends ExtensiveGameTerminal
{
  def payoff = {
    val outcomes: (Double, Double) = {
      playerOneType match {
        case IncompleteTypeOne => playerOneAction match {
          case IncompleteActionUp => playerTwoAction match {
            case IncompleteActionUp => (0, 0)
            case IncompleteActionDown => (2, 1)
//            case IncompleteActionDown => (0.5, -0.5)
          }
          case IncompleteActionDown => playerTwoAction match {
            case IncompleteActionUp => (1, 2)
//            case IncompleteActionUp => (-0.5, 0.5)
            case IncompleteActionDown => (3, 1)
//            case IncompleteActionDown => (1, -1)
          }
        }

        case IncompleteTypeTwo => playerOneAction match {
          case IncompleteActionUp => playerTwoAction match {
            case IncompleteActionUp => (2, 1)
//            case IncompleteActionUp => (0.5, -0.5)
            case IncompleteActionDown => (3, 1)
//            case IncompleteActionDown => (1, -1)
          }
          case IncompleteActionDown => playerTwoAction match {
            case IncompleteActionUp => (0, 0)
            case IncompleteActionDown => (1, 2)
//            case IncompleteActionDown => (-0.5, 0.5)
          }
        }
      }
    }

    ExpectedValue(Map(
      RationalPlayer(0) -> outcomes._1,
      RationalPlayer(1) -> outcomes._2))
  }
}

