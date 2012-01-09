package ao.learn.mst.cfr

import ao.learn.mst.gen.{ExtensiveGameNodeX, ExtensiveGameX}
import ao.learn.mst.gen.player.{ExtensivePlayerPosition, Rational, Chance}


//----------------------------------------------------------------------------------------------------------------------
object PlayerViewBuilder
{
  //--------------------------------------------------------------------------------------------------------------------
  def expand(root : ExtensiveGameNodeX, proponentIndex : Int) : PlayerViewNode =
  {
//    root.playerPartition match {
//      case Some(Chance) => new ChanceNode()
//
//      case Rational(ExtensivePlayerPosition( absolutePositionIndex )) => {
//        if (absolutePositionIndex == proponentIndex) {
//          new ProponentNode
//        } else {
//          new OpponentNode
//        }
//      }
//
//      case None => new TerminalNode()
//    }

    null
  }
}