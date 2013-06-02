package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen3.ProbabilityMass

/**
 * 11/05/13 5:49 PM
 */
trait ExtensiveStrategyProfile
{
  def actionProbabilities(
    informationSetIndex:Int,
    actionCount:Int
    ): ProbabilityMass
}
