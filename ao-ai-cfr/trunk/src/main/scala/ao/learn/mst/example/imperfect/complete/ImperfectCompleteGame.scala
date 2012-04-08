package ao.learn.mst.example.imperfect.complete

import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.example.perfect.complete.PerfectCompleteNode

/**
 * User: ao
 * Date: 07/04/12
 * Time: 5:09 PM
 */

object ImperfectCompleteGame
  extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayerCount = 2


  //--------------------------------------------------------------------------------------------------------------------
  def gameTreeRoot =
    ImperfectCompleteNode.root
}
