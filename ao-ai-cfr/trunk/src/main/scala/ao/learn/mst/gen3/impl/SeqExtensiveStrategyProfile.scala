package ao.learn.mst.gen3.impl

import ao.learn.mst.gen3.{ProbabilityMass, ExtensiveGameAbstraction, ExtensiveStrategyProfile}
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen2.player.model.FiniteAction

/**
 * 11/05/13 5:53 PM
 */
class SeqExtensiveStrategyProfile(probabilities:Seq[Seq[Double]])
  extends ExtensiveStrategyProfile
{
  def actionProbabilities
    [I <: InformationSet]
    (informationSet: I, abstraction: ExtensiveGameAbstraction[I, _]): ProbabilityMass =
      ProbabilityMass(probabilities(abstraction.infoSetIndex(informationSet)))
}
