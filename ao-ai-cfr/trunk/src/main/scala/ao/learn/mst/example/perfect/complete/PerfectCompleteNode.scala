package ao.learn.mst.example.perfect.complete

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.game.{ExtensiveGameDecision, ExtensiveGameTerminal, ExtensiveGameNode}
import ao.learn.mst.gen2.player.{DefaultFiniteAction, RationalPlayer, FiniteAction}


//----------------------------------------------------------------------------------------------------------------------
/**
 * Game tree for first game at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Perfect_and_complete_information
 */
case object PerfectCompleteNode {
  val root = PerfectCompleteDecisionFirst
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteDecisionFirst extends ExtensiveGameDecision {
  override def player =
    new RationalPlayer(0)

  def actions =
    DefaultFiniteAction.sequence(2)

  case object FirstDecisionInfoSet extends InformationSet
  def informationSet = FirstDecisionInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteDecisionAfterUp
      case 1 => PerfectCompleteDecisionAfterDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteDecisionAfterUp extends ExtensiveGameDecision {
  override def player =
    RationalPlayer(1)

  def actions =
    DefaultFiniteAction.sequence(2)

  case object AfterUpInfoSet extends InformationSet
  def informationSet = AfterUpInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteTerminalUpUp
      case 1 => PerfectCompleteTerminalUpDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteDecisionAfterDown extends ExtensiveGameDecision {
  override def player =
    RationalPlayer(1)

  def actions =
    DefaultFiniteAction.sequence(2)

  case object AfterDownInfoSet extends InformationSet
  def informationSet = AfterDownInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteTerminalDownUp
      case 1 => PerfectCompleteTerminalDownDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
case object PerfectCompleteTerminalUpUp extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 0, RationalPlayer(1) -> 0))
}

case object PerfectCompleteTerminalUpDown extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 2, RationalPlayer(1) -> 1))
}

case object PerfectCompleteTerminalDownUp extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 1, RationalPlayer(1) -> 2))
}

case object PerfectCompleteTerminalDownDown extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 3, RationalPlayer(1) -> 1))
}
