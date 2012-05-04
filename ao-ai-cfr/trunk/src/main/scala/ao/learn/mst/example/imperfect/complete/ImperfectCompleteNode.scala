package ao.learn.mst.example.imperfect.complete

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.game.{ExtensiveGameDecision, ExtensiveGameTerminal, ExtensiveGameNode}
import ao.learn.mst.gen2.player.{DefaultFiniteAction, RationalPlayer, FiniteAction}


//----------------------------------------------------------------------------------------------------------------------
/**
 * Game tree for first game at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Imperfect_information
 */
case object ImperfectCompleteNode {
  val root = ImperfectCompleteDecisionFirst
}


//----------------------------------------------------------------------------------------------------------------------
case object FirstDecisionInfoSet extends InformationSet
case object ImperfectCompleteDecisionFirst extends ExtensiveGameDecision {
  override def player =
    new RationalPlayer(0)

  def actions =
    DefaultFiniteAction.sequence(2)

  def informationSet = FirstDecisionInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteDecisionAfterUp
      case 1 => ImperfectCompleteDecisionAfterDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
case object AfterFirstInfoSet extends InformationSet

object ImperfectCompleteDecisionAfterUp extends ExtensiveGameDecision {
  override def player =
    RationalPlayer(1)

  def actions =
    DefaultFiniteAction.sequence(2)

  def informationSet = AfterFirstInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteTerminalUpUp
      case 1 => ImperfectCompleteTerminalUpDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectCompleteDecisionAfterDown extends ExtensiveGameDecision {
  override def player =
    RationalPlayer(1)

  def actions =
    Set(new FiniteAction(0),
        new FiniteAction(1))

  def informationSet = AfterFirstInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteTerminalDownUp
      case 1 => ImperfectCompleteTerminalDownDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectCompleteTerminalUpUp extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 0,
      RationalPlayer(1) -> 0))
}

object ImperfectCompleteTerminalUpDown extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 2,
      RationalPlayer(1) -> 1))
}

object ImperfectCompleteTerminalDownUp extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 1,
      RationalPlayer(1) -> 2))
}

object ImperfectCompleteTerminalDownDown extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 3,
      RationalPlayer(1) -> 1))
}

