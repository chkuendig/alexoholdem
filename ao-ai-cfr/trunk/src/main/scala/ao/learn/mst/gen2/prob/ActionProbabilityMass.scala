package ao.learn.mst.gen2.prob

import collection.immutable.SortedMap
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * Date: 22/11/11
 * Time: 5:23 AM
 */

case class ActionProbabilityMass(
  actionProbabilities : SortedMap[FiniteAction, Double])