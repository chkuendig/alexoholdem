package ao.learn.mst.example.incomplete

import ao.learn.mst.gen2.game.ExtensiveGame


//----------------------------------------------------------------------------------------------------------------------
object IncompleteGame
    extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayerCount = 2

  def gameTreeRoot =
    IncompleteNode.root
}
