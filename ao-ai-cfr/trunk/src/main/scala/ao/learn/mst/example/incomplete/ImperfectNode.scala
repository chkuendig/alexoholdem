package ao.learn.mst.example.incomplete

import ao.learn.mst.gen2.player.{RationalPlayer, FiniteAction}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.game.{ExtensiveGameChance, ExtensiveGameDecision, ExtensiveGameTerminal}
import ao.learn.mst.gen2.prob.ActionProbabilityMass
import collection.SortedSet


//----------------------------------------------------------------------------------------------------------------------
/**
 * Game tree for first game at:
 *  http://en.wikipedia.org/wiki/Extensive-form_game#Incomplete_information
 */
object ImperfectNode {
  val root = ImperfectCompleteDecisionFirst
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectChance extends ExtensiveGameChance {
  def actions =
    probabilities.probabilities.keySet

  def probabilities =
    ActionProbabilityMass(
      Map(new FiniteAction(0) → 0.5,
          new FiniteAction(1) → 0.5))

  override def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectPlayerTypeOne
      case 1 => ImperfectPlayerTypeTwo
    }
}


//----------------------------------------------------------------------------------------------------------------------
object ImperfectPlayerTypeOne extends ExtensiveGameDecision  {
  def actions = null

  def child(action: FiniteAction) = null

  override def player = null

  object InfoSet extends InformationSet
  def informationSet = InfoSet
}

object ImperfectPlayerTypeTwo extends ExtensiveGameDecision  {
  def actions = null

  def child(action: FiniteAction) = null

  override def player = null

  def informationSet = null
}



//----------------------------------------------------------------------------------------------------------------------
object ImperfectCompleteDecisionFirst extends ExtensiveGameDecision {
  override def player =
    new RationalPlayer(0)

  def actions =
    Set(new FiniteAction(0),
        new FiniteAction(1))

  object FirstDecisionInfoSet extends InformationSet
  def informationSet = FirstDecisionInfoSet

  def child(action: FiniteAction) =
    action.index match {
      case 0 => ImperfectCompleteDecisionAfterUp
      case 1 => ImperfectCompleteDecisionAfterDown
    }
}


//----------------------------------------------------------------------------------------------------------------------
object AfterFirstInfoSet extends InformationSet

object ImperfectCompleteDecisionAfterUp extends ExtensiveGameDecision {
  override def player =
    RationalPlayer(1)

  def actions =
    Set(new FiniteAction(0), new FiniteAction(1))

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

