package ao.learn.mst.gen3.strategy

import ao.learn.mst.gen3.ProbabilityMass


/**
 * 01/06/13 5:23 PM
 */
trait CfrStrategyProfile
{
  def toExtensiveStrategyProfile: ExtensiveStrategyProfile

  def update(
    informationSetIndex : Int,
    actionRegret        : Seq[Double],
    reachProbability    : Double)

  def positiveRegretStrategy(
    informationSetIndex:Int,
    actionCount:Int
    ): ProbabilityMass
}
