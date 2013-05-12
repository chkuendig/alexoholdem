package ao.learn.mst.gen3

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * 11/05/13 5:49 PM
 */
trait ExtensiveStrategyProfile
{
  def actionProbabilities
    [I <: InformationSet]
    (informationSet:I, abstraction:ExtensiveGameAbstraction[I, _]): ProbabilityMass
}
