package ao.learn.mst.example.perfect.complete.decision

import ao.learn.mst.gen2.game.ExtensiveGameDecision
import ao.learn.mst.gen2.player.{NamedFiniteAction, FiniteAction, RationalPlayer}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.example.perfect.complete.terminal.{PerfectCompleteTerminalDownDown, PerfectCompleteTerminalDownUp, PerfectCompleteTerminalUpDown, PerfectCompleteTerminalUpUp}


//----------------------------------------------------------------------------------------------------------------------
abstract class PerfectCompleteDecision
    extends ExtensiveGameDecision
{
  def actions =
    NamedFiniteAction.sequence(
      "U", "D")
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteDecisionFirst
    extends PerfectCompleteDecision
{
  override def player =
    new RationalPlayer(0)

  case object FirstDecisionInfoSet extends InformationSet
  def informationSet = FirstDecisionInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteDecisionAfterUp
      case 1 => PerfectCompleteDecisionAfterDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteDecisionAfterUp
    extends PerfectCompleteDecision
{
  override def player =
    RationalPlayer(1)

  case object AfterUpInfoSet extends InformationSet
  def informationSet = AfterUpInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteTerminalUpUp
      case 1 => PerfectCompleteTerminalUpDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteDecisionAfterDown
    extends PerfectCompleteDecision
{
  override def player =
    RationalPlayer(1)

  case object AfterDownInfoSet extends InformationSet
  def informationSet = AfterDownInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteTerminalDownUp
      case 1 => PerfectCompleteTerminalDownDown
    }
}


