package ao.learn.mst.gen3.abstraction

import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.gen3.representation.ExtensiveStateDescriber
import ao.learn.mst.gen2.info.InformationSet
import ao.learn.mst.gen3.ExtensiveAction

/**
 * 01/06/13 8:59 PM
 */
trait ExtensiveGameAbstractionBuilder
{
  def buildExtensiveGameAbstraction
    [I <: InformationSet, A <: ExtensiveAction] (
      extensiveGame: ExtensiveGame,
      informationSetDescriber: ExtensiveStateDescriber[I, A]
    ): ExtensiveGameAbstraction[I, A]
}
