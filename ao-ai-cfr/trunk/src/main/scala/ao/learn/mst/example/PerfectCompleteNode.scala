package ao.learn.mst.example

import ao.learn.mst.gen2.player.{RationalPlayer, FiniteAction}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.game.{ExtensiveGameDecision, ExtensiveGameTerminal, ExtensiveGameNode}


//----------------------------------------------------------------------------------------------------------------------
/**
 * Game tree for first game at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Perfect_and_complete_information
 */
object PerfectCompleteNode
{
  val root = PerfectCompleteDecisionFirst
}


//----------------------------------------------------------------------------------------------------------------------
object PerfectCompleteDecisionFirst extends ExtensiveGameDecision
{
  override def player =
    new RationalPlayer(0)

  def actions =
    Set(new FiniteAction(0),
        new FiniteAction(1))

  object FirstDecisionInfoSet extends InformationSet
  def informationSet = FirstDecisionInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteDecisionAfterUp
      case 1 => PerfectCompleteDecisionAfterDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object PerfectCompleteDecisionAfterUp extends ExtensiveGameDecision
{
  override def player =
    RationalPlayer(1)

  def actions =
    Set(new FiniteAction(0),
        new FiniteAction(1))

  object AfterUpInfoSet extends InformationSet
  def informationSet = AfterUpInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteTerminalUpUp
      case 1 => PerfectCompleteTerminalUpDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object PerfectCompleteDecisionAfterDown extends ExtensiveGameDecision
{
  override def player =
    RationalPlayer(1)

  def actions =
    Set(new FiniteAction(0),
        new FiniteAction(1))

  object AfterDownInfoSet extends InformationSet
  def informationSet = AfterDownInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => PerfectCompleteTerminalDownUp
      case 1 => PerfectCompleteTerminalDownDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object PerfectCompleteTerminalUpUp extends ExtensiveGameTerminal
{
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 0,
        RationalPlayer(1) -> 0))
}

object PerfectCompleteTerminalUpDown extends ExtensiveGameTerminal
{
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 2,
        RationalPlayer(1) -> 1))
}

object PerfectCompleteTerminalDownUp extends ExtensiveGameTerminal
{
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 1,
        RationalPlayer(1) -> 2))
}

object PerfectCompleteTerminalDownDown extends ExtensiveGameTerminal
{
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> 3,
        RationalPlayer(1) -> 1))
}
