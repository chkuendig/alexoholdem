package ao.holdem.gen.abs

import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.holdem.gen.{DecisionAction, HoldemAction, HoldemInfo}
import ao.holdem.model.Round
import ao.holdem.bot.simple.starting_hands.Sklansky




object SklanskyInfoAbstraction
  extends ExtensiveAbstraction[HoldemInfo, HoldemAction]
{
  def informationSetIndex(informationSet: HoldemInfo): Int = {
    if (informationSet.actionSequence.round() != Round.PREFLOP) {
      0
    } else {
      val group: Int =
        Sklansky.groupOf(informationSet.hole)

      group
    }
  }

  def actionIndex(action: HoldemAction): Int =
    action match {
      case DecisionAction(choice) =>
        choice.ordinal()

      case _ => throw new Error
    }

  def actionSubIndex(informationSet: HoldemInfo, action: HoldemAction): Int =
    actionIndex(action)

  def actionCount(informationSet: HoldemInfo): Int =
    3
}
