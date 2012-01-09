package ao.learn.mst.kuhn.adapt.v2

import ao.learn.mst.gen2.game.ExtensiveGame
import ao.learn.mst.gen2.player.RationalPlayer
import ao.learn.mst.gen.utility.ExtensiveUtilityRange._

/**
 * Date: 03/12/11
 * Time: 7:51 PM
 */

class KuhnGame
    extends ExtensiveGame
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayers =
    Set(RationalPlayer(0),
        RationalPlayer(1))

//  def utilityRanges = {
//    val utilityRange =
//      ExtensiveUtilityRange(-2.0, 2.0)
//
//    Seq.fill(2)(utilityRange)
//  }

  def gameTreeRoot = null
}