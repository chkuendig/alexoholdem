package ao.learn.mst.gen2.game

import ao.learn.mst.gen2.player.{FiniteAction, RationalPlayer}
import ao.learn.mst.gen2.info.InformationSet


/**
 * Date: 03/12/11
 * Time: 7:29 PM
 */

trait ExtensiveGameDecision
    extends ExtensiveGameNonTerminal
{
  //--------------------------------------------------------------------------------------------------------------------
  override def player : RationalPlayer


  //--------------------------------------------------------------------------------------------------------------------
//  override def child(decision : FiniteAction) : ExtensiveGameNode


  //--------------------------------------------------------------------------------------------------------------------
  def informationSet : InformationSet
}