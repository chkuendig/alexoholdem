package ao.learn.mst.gen2.solve

import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.gen2.info.{InformationSet, InformationSetIndex}
import ao.learn.mst.cfr.StrategyProfile

//----------------------------------------------------------------------------------------------------------------------
trait ExtensiveGameSolver
{
  def reduceRegret(
    game                : ExtensiveGame,
    informationSetIndex : InformationSetIndex[InformationSet],
    strategyProfile     : StrategyProfile[InformationSet])
}