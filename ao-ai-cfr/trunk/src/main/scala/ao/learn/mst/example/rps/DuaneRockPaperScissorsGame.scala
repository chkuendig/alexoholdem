
package ao.learn.mst.example.rps

import ao.learn.mst.gen2.game.ExtensiveGame


//----------------------------------------------------------------------------------------------------------------------
case object DuaneRockPaperScissorsGame
    extends ExtensiveGame
{
  def rationalPlayerCount = 2

  def gameTreeRoot =
    DuaneRockPaperScissorsNode.root
}