package ao.holdem.gen.abs

import ao.holdem.ai.abs.StateAbstraction
import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.holdem.gen.{DecisionAction, HoldemAction, HoldemInfo}
import ao.holdem.model.card.sequence.CardSequence


class GenStateAbstraction(abstraction: StateAbstraction)
    extends ExtensiveAbstraction[HoldemInfo, HoldemAction]
{
  def informationSetIndex(informationSet: HoldemInfo): Int = {
    abstraction.indexOf(
      informationSet.actionSequence.state(),
      new CardSequence(informationSet.hole, informationSet.community))
  }


  def actionIndex(action: HoldemAction): Int =
    action match {
      case DecisionAction(choice) =>
        choice.ordinal()

      case _ => throw new Error
    }


  def actionSubIndex(informationSet: HoldemInfo, action: HoldemAction): Int =
    actionIndex(action)


  def actionCount(informationSet: HoldemInfo): Int = {
    informationSet.actionSequence.acts().size()
  }
}
