package ao.learn.mst.gen3.strategy.impl

import ao.learn.mst.gen3.{ProbabilityMass}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.player.model.FiniteAction
import ao.learn.mst.gen3.strategy.ExtensiveStrategyProfile

/**
 * 11/05/13 5:53 PM
 */
case class SeqExtensiveStrategyProfile(probabilities:Seq[Seq[Double]])
  extends ExtensiveStrategyProfile
{
  def actionProbabilities(
      informationSetIndex:Int, actionCount:Int): ProbabilityMass =
  {
    val actionProbabilities:Seq[Double] = probabilities(informationSetIndex)

    if (actionProbabilities == null || actionProbabilities.length != actionCount) {
      throw new IllegalArgumentException("Unexpected number of actions: " +
        informationSetIndex + " | " + actionCount + " | " + actionProbabilities)
    }

    ProbabilityMass(actionProbabilities)
  }
}
