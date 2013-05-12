package ao.learn.mst.example.slot.general

import ao.learn.mst.gen2.solve.ExpectedValue
import ao.learn.mst.gen2.player.model.RationalPlayer
import ao.learn.mst.gen2.game.node.ExtensiveGameTerminal

//--------------------------------------------------------------------------------------------------------------------
case class SlotMachineTerminal(value: Double) extends ExtensiveGameTerminal {
  def payoff = ExpectedValue(
    Map(RationalPlayer(0) -> value))
}
