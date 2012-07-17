package ao.learn.mst.gen2.prob

import ao.learn.mst.gen2.player.FiniteAction
import collection.immutable.SortedMap

/**
 * Date: 22/11/11
 * Time: 5:23 AM
 */

case class ActionProbabilityMass(
  actionProbabilities : SortedMap[FiniteAction, Double])