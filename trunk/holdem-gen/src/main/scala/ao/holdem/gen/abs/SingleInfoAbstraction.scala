package ao.holdem.gen.abs

import ao.learn.mst.gen5.ExtensiveAbstraction
import ao.holdem.gen.{DecisionAction, HoldemAction, HoldemInfo}

/**
 * 
 */
object SingleInfoAbstraction
    extends ExtensiveAbstraction[HoldemInfo, HoldemAction]
{
  def informationSetIndex(informationSet: HoldemInfo): Int =
    0

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
