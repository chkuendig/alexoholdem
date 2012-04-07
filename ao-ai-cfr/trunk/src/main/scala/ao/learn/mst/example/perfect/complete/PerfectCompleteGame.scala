package ao.learn.mst.example.perfect.complete

import ao.learn.mst.gen2.game.ExtensiveGame

/**
 * User: ao
 * Date: 11/03/12
 * Time: 8:48 PM
 */
class PerfectCompleteGame
  extends ExtensiveGame {
  //--------------------------------------------------------------------------------------------------------------------
  //  def rationalPlayers = null
  def rationalPlayerCount = 2


  //--------------------------------------------------------------------------------------------------------------------
  def gameTreeRoot =
    PerfectCompleteNode.root
}
