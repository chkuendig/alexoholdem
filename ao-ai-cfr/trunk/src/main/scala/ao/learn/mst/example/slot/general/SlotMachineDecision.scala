package ao.learn.mst.example.slot.general

import ao.learn.mst.gen2.player.RationalPlayer
import ao.learn.mst.gen2.game.ExtensiveGameDecision


abstract class SlotMachineDecision extends ExtensiveGameDecision
{
  override def player = RationalPlayer(0)

  def informationSet = SlotMachineInfoSet
}