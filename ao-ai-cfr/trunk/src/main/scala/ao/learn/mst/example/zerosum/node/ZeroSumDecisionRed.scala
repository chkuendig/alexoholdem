package ao.learn.mst.example.zerosum.node

import collection.immutable.SortedSet
import ao.learn.mst.example.zerosum.act._
import ao.learn.mst.example.zerosum.info.ZeroSumInfoRed
import ao.learn.mst.gen2.player.model.{RationalPlayer, FiniteAction}
import ao.learn.mst.gen2.game.node.ExtensiveGameDecision


//----------------------------------------------------------------------------------------------------------------------
case object ZeroSumDecisionRed
  extends ExtensiveGameDecision
{
  val actions =
    SortedSet[FiniteAction](
      ZeroSumActionRedOne, ZeroSumActionRedTwo)

  def child(action: FiniteAction) = action.index match {
    case ZeroSumActionRedOne.index => ZeroSumDecisionBlue(ZeroSumActionRedOne)
    case ZeroSumActionRedTwo.index => ZeroSumDecisionBlue(ZeroSumActionRedTwo)
  }

  override val player =
    RationalPlayer(0)

  val informationSet = ZeroSumInfoRed
}
