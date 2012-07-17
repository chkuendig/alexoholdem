package ao.learn.mst.kuhn.adapt

import ao.learn.mst.gen.utility.ExtensiveUtilityRange
import ao.learn.mst.gen.ExtensiveGameX

/**
 * Date: 15/11/11
 * Time: 1:23 AM
 */

class KuhnExtensiveGameX
  extends ExtensiveGameX
{
  //--------------------------------------------------------------------------------------------------------------------
  def rationalPlayerCount = 2

  def utilityRanges = {
    val utilityRange =
      ExtensiveUtilityRange(-2.0, 2.0)

    Seq.fill(2)(utilityRange)
  }


  //--------------------------------------------------------------------------------------------------------------------
  def gameTreeRoot = FirstPlayerCardDeal
}