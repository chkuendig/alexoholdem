package ao.learn.mst.gen3.representation

import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.ExtensiveAction

/**
 * 01/06/13 8:55 PM
 */
trait ExtensiveStateDescriber[I <: InformationSet, A <: ExtensiveAction]
{
  def describe(informationSet: I): ExtensiveStateView[I, A]
}
