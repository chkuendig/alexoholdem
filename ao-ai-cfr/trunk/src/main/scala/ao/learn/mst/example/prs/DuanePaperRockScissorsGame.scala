
package ao.learn.mst.example.prs

import ao.learn.mst.gen2.game.ExtensiveGame


//----------------------------------------------------------------------------------------------------------------------
object DuanePaperRockScissorsGame
    extends ExtensiveGame
{
  def rationalPlayerCount = 2

  def gameTreeRoot =
    DuanePaperRockScissorsNode.root
}